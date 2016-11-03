from django.shortcuts import render
from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect
from django.http import HttpResponse
from django.template import loader
from django.contrib import auth
from django.contrib.auth.forms import  UserCreationForm
from .forms import MyForm
from django.contrib import messages
from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.http import HttpResponsePermanentRedirect as PerRedirect, HttpResponse
from datetime import datetime, timedelta
import csv

from Feeder.settings import STUDENT_CSV
from special_admin.models import Student, Course, Task, FeedBackForm, SpecialAdmin,FeedbackResponse
from special_admin.static_strings import SessionKeys as sk, FeedbackStrings as fbs
from special_admin import datetime_helper

#need to check whether to add extra project




dict = {}
# Create your views here.
def login(request):
    #template = loader.get_template('instructor_login/login.html')

    return render(request,'instructor_login/login.html',{'user': request.user})

def auth_view(request):
    username = request.POST.get('username','')
    password = request.POST.get('password','')
    user = auth.authenticate(username = username,password = password)

    if user is not None:
        auth.login(request,user)
        url = '/instructor_login/loggedin/'
        return HttpResponseRedirect(url)
    else:
        url = '/instructor_login/invalid/'
        return HttpResponseRedirect(url)

def loggedin(request):
    #template = loader.get_template('instructor_login/loggedin.html')
    context = {'full_name' : request.user.username}
    return render(request,'instructor_login/loggedin.html',{'user': request.user})

def invalid(request):
    #template = loader.get_template('instructor_login/loggedin.html')
    #context = {'full_name' : request.user.username}
    return render(request,'instructor_login/invalid.html')

def logout(request):
    #template = loader.get_template('instructor_login/loggedin.html')
    auth.logout(request)
    return render(request,'instructor_login/logout.html')


def register(request):
    #template = loader.get_template('instructor_login/loggedin.html')

    if request.method == 'POST':
        form = MyForm(request.POST)
        if form.is_valid():
            user = form.save(commit=False)
            username = form.cleaned_data['username']
            password = form.cleaned_data['password1']
            user.set_password(password)

            form.save()
            return render(request, 'instructor_login/register_success.html', {'form': form})
        else:
            return render(request, 'instructor_login/register_failure.html', {'form': form})




    args = {}
    args['form'] = MyForm()
    return render(request, 'instructor_login/register.html', args)


def register_success(request):
    #template = loader.get_template('instructor_login/loggedin.html')

    return render(request,'instructor_login/register_success.html')

def register_failure(request):
    #template = loader.get_template('instructor_login/loggedin.html')

    return render(request,'instructor_login/register_failure.html',dict)

def tasks(request, pk):
    all_courses = Course.objects.all()
    selected_course = get_object_or_404(Course, pk=pk)
    selected_course_tasks = selected_course.task_set.all()
    finished_tasks = []
    remaining_tasks = []
    present_datetime = datetime.now()
    for task_iter in selected_course_tasks:
        task_deadline = datetime_helper.get_datetime(task_iter.deadline)
        if task_deadline <= present_datetime:
            finished_tasks.append(task_iter)
        else:
            remaining_tasks.append(task_iter)

    if request.method == 'POST':
        if 'delete' in request.POST:
            delete_task = get_object_or_404(Task, pk=request.POST['delete'])
            delete_task.delete()
            selected_course_tasks = selected_course.task_set.all()
            finished_tasks = []
            remaining_tasks = []
            present_datetime = datetime.now()
            for task_iter in selected_course_tasks:
                task_deadline = datetime_helper.get_datetime(task_iter.deadline)
                if task_deadline <= present_datetime:
                    finished_tasks.append(task_iter)
                else:
                    remaining_tasks.append(task_iter)

            return render(request, 'instructor_login/task_crud/tasks.html', {'course': selected_course,
                                                                          'all_course': all_courses,
                                                                          'finished_tasks': finished_tasks,
                                                                          'remaining_tasks': remaining_tasks,
                                                                          'total': len(finished_tasks) + len(
                                                                              remaining_tasks)})

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
        return render(request, 'instructor_login/task_crud/tasks.html', {'course': selected_course,
                                                                          'all_course': all_courses,
                                                                          'finished_tasks': finished_tasks,
                                                                          'remaining_tasks': remaining_tasks,
                                                                          'total': len(finished_tasks) + len(
                                                                              remaining_tasks)})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')


def new_task(request, pk):
    course = get_object_or_404(Course, pk=pk)

    if request.method == 'POST':
        if 'task_tag' in request.POST:
            try:
                Task.objects.get(tag=request.POST['task_tag'])
                return render(request, 'instructor_login/task_crud/new_task.html', {'course': course,
                                                                                 'message': 'Task with same Tag already exists'})
            # adding new task
            except ObjectDoesNotExist:
                # changing formats
                date_deadline_str = request.POST['task_deadline_date']
                date_deadline_parts = date_deadline_str.split("-")
                date_deadline = date_deadline_parts[2] + "/" + date_deadline_parts[1] + "/" + date_deadline_parts[0]
                # now date is of format dd/mm/yyyy
                time_deadline_str = request.POST['task_deadline_time']
                time_deadline = time_deadline_str + ":00"
                # now time is of format hh:mm:ss
                date_time_deadline = date_deadline + " " + time_deadline

                _new_task = Task(tag=request.POST['task_tag'], detail=request.POST['task_detail'],
                                 deadline=date_time_deadline)
                # saving task
                _new_task.save()

                # adding to task set of course
                course.task_set.add(_new_task)

                return PerRedirect(reverse('instructor_login:tasks', kwargs={'pk': course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
            return render(request, 'instructor_login/task_crud/new_task.html', {'course': course})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')


def old_task(request, pk):
    selected_task = get_object_or_404(Task, pk=pk)
    selected_course = Course.objects.get(pk=selected_task.course_id)
    date_time_parts = selected_task.deadline.split(" ")
    date = date_time_parts[0].split("/")
    time = date_time_parts[1].split(":")

    if request.method == 'POST':
        # delete student
        if 'delete_task' in request.POST:
            selected_task.delete()
            return PerRedirect(reverse('instructor_login:tasks', kwargs={'pk': selected_course.pk}))

        if 'task_tag' in request.POST:
            try:
                Task.objects.get(tag=request.POST['task_tag'])

                # the same tag exists for some other event
                if request.POST['task_tag'] != selected_task.tag:
                    # special admin logged in
                    #if request.session.get(sk.instructor_login_logged, False):
                    return render(request, 'instructor_login/task_crud/old_task.html', {'task': selected_task,
                                                                                         'year': date[2],
                                                                                         'month': date[1],
                                                                                         'day': date[0],
                                                                                         'hour': time[0],
                                                                                         'minute': time[1],
                                                                                         'course': selected_course,
                                                                                         'message': 'Task with same Tag already exists'})
                # exists for the same event
                else:

                    # updating old task
                    # changing formats
                    date_deadline_str = request.POST['task_deadline_date']
                    date_deadline_parts = date_deadline_str.split("-")
                    date_deadline = date_deadline_parts[2] + "/" + date_deadline_parts[1] + "/" + date_deadline_parts[0]
                    # now date is of format dd/mm/yyyy
                    time_deadline_str = request.POST['task_deadline_time']
                    time_deadline = time_deadline_str + ":00"
                    # now time is of format hh:mm:ss
                    date_time_deadline = date_deadline + " " + time_deadline

                    selected_task.tag = request.POST['task_tag']
                    selected_task.detail = request.POST['task_detail']
                    selected_task.deadline = date_time_deadline
                    # saving task
                    selected_task.save()

                    date_time_parts = selected_task.deadline.split(" ")
                    date = date_time_parts[0].split("/")
                    time = date_time_parts[1].split(":")

                    return render(request, 'instructor_login/task_crud/old_task.html', {'task': selected_task,
                                                                                     'year': date[2],
                                                                                     'month': date[1],
                                                                                     'day': date[0],
                                                                                     'hour': time[0],
                                                                                     'minute': time[1],
                                                                                     'course': selected_course,
                                                                                     'message': 'updated'})

            except ObjectDoesNotExist:
                # updating old task
                # changing formats
                date_deadline_str = request.POST['task_deadline_date']
                date_deadline_parts = date_deadline_str.split("-")
                date_deadline = date_deadline_parts[2] + "/" + date_deadline_parts[1] + "/" + date_deadline_parts[0]
                # now date is of format dd/mm/yyyy
                time_deadline_str = request.POST['task_deadline_time']
                time_deadline = time_deadline_str + ":00"
                # now time is of format hh:mm:ss
                date_time_deadline = date_deadline + " " + time_deadline

                selected_task.tag = request.POST['task_tag']
                selected_task.detail = request.POST['task_detail']
                selected_task.deadline = date_time_deadline
                # saving task
                selected_task.save()

                date_time_parts = selected_task.deadline.split(" ")
                date = date_time_parts[0].split("/")
                time = date_time_parts[1].split(":")

                return render(request, 'instructor_login/task_crud/old_task.html', {'task': selected_task,
                                                                                 'year': date[2],
                                                                                 'month': date[1],
                                                                                 'day': date[0],
                                                                                 'hour': time[0],
                                                                                 'minute': time[1],
                                                                                 'course': selected_course,
                                                                                 'message': 'updated'})

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
        return render(request, 'instructor_login/task_crud/old_task.html', {'task': selected_task,
                                                                             'year': date[2],
                                                                             'month': date[1],
                                                                             'day': date[0],
                                                                             'hour': time[0],
                                                                             'minute': time[1],
                                                                             'course': selected_course})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')



def tasks_redirect(request):
    # special admin logged in

        all_courses = Course.objects.all()
        if all_courses:
            pass
            return PerRedirect(reverse('instructor_login:tasks', kwargs={'pk': all_courses[0].pk}))
        else:
            return render(request, 'instructor_login/home.html', {'message': 'No tasks available'})

    # NOT logged in

def courses(request):
    # if request.method == 'POST':
    #     if 'delete' in request.POST:
    #         delete_course = get_object_or_404(Course, pk=request.POST['delete'])
    #         delete_course.delete()
    #         all_courses = Course.objects.all()
    #         return render(request, 'instructor_login/course_crud/courses.html', {'all_courses': all_courses})
    #
    # elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.special_admin_logged, False):
        all_courses = Course.objects.all()
        return render(request, 'instructor_login/course_crud/courses.html', {'all_courses': all_courses})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')



def feedback_forms(request, pk):
    all_courses = Course.objects.all()
    selected_course = get_object_or_404(Course, pk=pk)
    selected_course_fb_forms = selected_course.feedbackform_set.all()
    finished_fb_forms = []
    remaining_fb_forms = []
    present_datetime = datetime.now()
    for fb_form_iter in selected_course_fb_forms:
        fb_form_deadline = datetime_helper.get_datetime(fb_form_iter.deadline)
        if fb_form_deadline <= present_datetime:
            finished_fb_forms.append(fb_form_iter)
        else:
            remaining_fb_forms.append(fb_form_iter)

    if request.method == 'POST':
        if 'delete' in request.POST:
            delete_fb_form = get_object_or_404(FeedBackForm, pk=request.POST['delete'])
            delete_fb_form.delete()
            selected_course_fb_forms = selected_course.feedbackform_set.all()
            finished_fb_forms = []
            remaining_fb_forms = []
            present_datetime = datetime.now()
            for fb_form_iter in selected_course_fb_forms:
                fb_form_deadline = datetime_helper.get_datetime(fb_form_iter.deadline)
                if fb_form_deadline <= present_datetime:
                    finished_fb_forms.append(fb_form_iter)
                else:
                    remaining_fb_forms.append(fb_form_iter)
            return render(request, 'instructor_login/fb_form_crud/fb_forms.html', {'course': selected_course,
                                                                                'all_course': all_courses,
                                                                                'finished_fb_forms': finished_fb_forms,
                                                                                'remaining_fb_forms': remaining_fb_forms,
                                                                                'total': len(finished_fb_forms) + len(
                                                                                    remaining_fb_forms)})

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
        return render(request, 'instructor_login/fb_form_crud/fb_forms.html', {'course': selected_course,
                                                                                'all_course': all_courses,
                                                                                'finished_fb_forms': finished_fb_forms,
                                                                                'remaining_fb_forms': remaining_fb_forms,
                                                                                'total': len(finished_fb_forms) + len(
                                                                                    remaining_fb_forms)})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')


def new_feedback_form(request, pk):
    course = get_object_or_404(Course, pk=pk)

    if request.method == 'POST':
        if 'fb_form_name' in request.POST:
            try:
                FeedBackForm.objects.get(name=request.POST['fb_form_name'])
                return render(request, 'instructor_login/fb_form_crud/new_fb_form.html', {'course': course,
                                                                                       'delimiter': fbs.form_delimiter,
                                                                                       'message': 'Feedback form with same Name already exists'})
            # adding new fb form
            except ObjectDoesNotExist:
                # changing formats
                date_deadline_str = request.POST['fb_form_deadline_date']
                date_deadline_parts = date_deadline_str.split("-")
                date_deadline = date_deadline_parts[2] + "/" + date_deadline_parts[1] + "/" + date_deadline_parts[0]
                # now date is of format dd/mm/yyyy
                time_deadline_str = request.POST['fb_form_deadline_time']
                time_deadline = time_deadline_str + ":00"
                # now time is of format hh:mm:ss
                date_time_deadline = date_deadline + " " + time_deadline

                _new_fb_form = FeedBackForm(name=request.POST['fb_form_name'],
                                            question_set=request.POST['question_set'], deadline=date_time_deadline)
                # saving task
                _new_fb_form.save()

                # adding to task set of course
                course.feedbackform_set.add(_new_fb_form)

                return PerRedirect(reverse('instructor_login:feedback_forms', kwargs={'pk': course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
        return render(request, 'instructor_login/fb_form_crud/new_fb_form.html', {'course': course,
                                                                                   'delimiter': fbs.form_delimiter})
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')


def old_feedback_form(request, pk):
    selected_fb_form = get_object_or_404(FeedBackForm, pk=pk)
    selected_course = Course.objects.get(pk=selected_fb_form.course_id)
    date_time_parts = selected_fb_form.deadline.split(" ")
    date = date_time_parts[0].split("/")
    time = date_time_parts[1].split(":")
    question_set = selected_fb_form.question_set.split(fbs.form_delimiter)
    all_feedback_responses = FeedbackResponse.objects.filter(feedback_form_id=selected_fb_form.pk)
    qsnlen = len(question_set)
    response_count = 0
    ans_dict = {}
    num_dict = {}
    text_response = []



    # for qsn in question_set:
    #     ans_dict[qsn] = [0]*6
    for i in range(0,qsnlen):
        num_dict[i] = [0] * 6

    # for response_data in all_feedback_responses:
    #     student_response = response_data.answer_set.split(fbs.form_delimiter)
    #     student_response = [int(float(i)) for i in student_response]
    #     extra_response = response_data.comment
    #     if len(extra_response) > 0 :
    #         text_response.append(extra_response)
    #     print(student_response)
    #     i = 0
    #     for qsn in question_set:
    #         ans_dict[qsn][student_response[question_set.index(qsn)]] = ans_dict[qsn][student_response[question_set.index(qsn)]] + 1
    #
    #     response_count = response_count + 1

    for response_data in all_feedback_responses:
        student_response = response_data.answer_set.split(fbs.form_delimiter)
        student_response = [int(float(i)) for i in student_response]
        extra_response = response_data.comment
        if len(extra_response) > 0:
            text_response.append(extra_response)
        print(student_response)

        for j in range(0,qsnlen):
            num_dict[j][student_response[j]] = num_dict[j][student_response[j]] + 1


        response_count = response_count + 1

    # print(ans_dict)

    final_dict = {'fb_form': selected_fb_form,
                 'year': date[2],
                 'month': date[1],
                 'day': date[0],
                 'hour': time[0],
                 'minute': time[1],
                 'course': selected_course,
                 'question_set': question_set,
                 'response_count': response_count,
                  'num_dict':num_dict,
                  'text_response' : text_response}

    #final_dict.update(num_dict)




    if request.method == 'POST':
        # delete student
        if 'delete_fb_form' in request.POST:
            selected_fb_form.delete()
            return PerRedirect(reverse('instructor_login:feedback_forms', kwargs={'pk': selected_course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        #if request.session.get(sk.instructor_login_logged, False):
        return render(request, 'instructor_login/fb_form_crud/old_fb_form.html',final_dict )
        # NOT logged in
        # else:
        #     return render(request, 'instructor_login/login.html')



