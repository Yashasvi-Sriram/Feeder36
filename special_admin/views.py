from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.http import HttpResponsePermanentRedirect as PerRedirect, HttpResponse
from datetime import datetime, timedelta

from .models import Student, Course, Task, FeedBackForm, SpecialAdmin
from .static_strings import SessionKeys as sk, FeedbackStrings as fbs
from . import datetime_helper


def home(request):
    # POST request
    if request.method == 'POST':

        # Login request
        if 'special_admin_email' in request.POST and 'special_admin_password' in request.POST:
            # Validating SuperUser credentials from the fields statically typed in code
            error_set = {}
            if request.POST['special_admin_email'] != SpecialAdmin.email:
                error_set['email_incorrect'] = 'Email incorrect'
            if request.POST['special_admin_password'] != SpecialAdmin.password:
                error_set['password_incorrect'] = 'Password incorrect'
            if len(error_set) > 0:
                return render(request, 'special_admin/login.html', error_set)
            # Validating finished

            request.session['special_admin_logged'] = True
            return render(request, 'special_admin/home.html')

        # Logout request
        if 'log_out' in request.POST and request.POST['log_out'] == 'True':
            request.session['special_admin_logged'] = False
            return render(request, 'special_admin/login.html')

    # GET request
    elif request.method == 'GET':

        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/home.html')
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def students(request):
    if request.method == 'POST':
        if 'delete' in request.POST:
            delete_student = get_object_or_404(Student, pk=request.POST['delete'])
            delete_student.delete()
            all_students = Student.objects.all()
            return render(request, 'special_admin/student_crud/students.html', {'all_students': all_students})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            all_students = Student.objects.all()
            return render(request, 'special_admin/student_crud/students.html', {'all_students': all_students})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def new_student(request):
    all_courses = Course.objects.all()

    if request.method == 'POST':
        if 'courses' in request.POST:
            try:
                Student.objects.get(user_name=request.POST['student_user_name'])
                return render(request, 'special_admin/student_crud/new_student.html', {'all_courses': all_courses,
                                                                                       'message': 'User Name Already Exists'})
            except ObjectDoesNotExist:
                _new_student = Student(name=request.POST['student_name'], user_name=request.POST['student_user_name'])
                _new_student.save()

                courses_pks_str = request.POST['courses']
                courses_pks = courses_pks_str.split(',')
                if not courses_pks_str == '':
                    for course_pk in courses_pks:
                        course = Course.objects.get(pk=course_pk)
                        _new_student.course_set.add(course)
                        course.student_set.add(_new_student)

                return PerRedirect(reverse('special_admin:students'))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/student_crud/new_student.html', {'all_courses': all_courses})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def old_student(request, pk):
    all_courses = Course.objects.all()
    selected_student = get_object_or_404(Student, pk=pk)

    if request.method == 'POST':
        # delete student
        if 'delete_student' in request.POST:
            selected_student.delete()
            return PerRedirect(reverse('special_admin:students'))

        # update student
        if 'courses' in request.POST:

            try:
                Student.objects.get(user_name=request.POST['student_user_name'])

                # user_name exists for some other fellow
                if request.POST['student_user_name'] != selected_student.user_name:
                    # getting new values
                    signed_courses = selected_student.course_set.all()
                    unsigned_courses = [course for course in all_courses if course not in signed_courses]
                    return render(request, 'special_admin/student_crud/old_student.html', {'student': selected_student,
                                                                                           'signed_courses': signed_courses,
                                                                                           'unsigned_courses': unsigned_courses,
                                                                                           'all_courses': all_courses,
                                                                                           'message': 'User Name Already Exists'})
                # user_name exists but for same fellow
                else:
                    # deleting previous course set
                    selected_student.course_set.clear()
                    # removing the current student from student sets of all courses
                    for course in all_courses:
                        course.student_set.remove(selected_student)

                    # update basic info
                    selected_student.name = request.POST['student_name']
                    selected_student.user_name = request.POST['student_user_name']

                    # update course set
                    courses_pks_str = request.POST['courses']
                    courses_pks = courses_pks_str.split(',')
                    selected_student.course_set.clear()

                    # only needed if course set is non empty
                    if not courses_pks_str == '':
                        for course_pk in courses_pks:
                            course = Course.objects.get(pk=course_pk)
                            selected_student.course_set.add(course)
                            course.student_set.add(selected_student)
                            course.save()

                    selected_student.save()

                    # getting new values
                    signed_courses = selected_student.course_set.all()
                    unsigned_courses = [course for course in all_courses if course not in signed_courses]
                    return render(request, 'special_admin/student_crud/old_student.html', {'student': selected_student,
                                                                                           'signed_courses': signed_courses,
                                                                                           'unsigned_courses': unsigned_courses,
                                                                                           'all_courses': all_courses,
                                                                                           'message': 'updated'})
            except ObjectDoesNotExist:
                # deleting previous course set
                selected_student.course_set.clear()
                # removing the current student from student sets of all courses
                for course in all_courses:
                    course.student_set.remove(selected_student)

                # update basic info
                selected_student.name = request.POST['student_name']
                selected_student.user_name = request.POST['student_user_name']

                # update course set
                courses_pks_str = request.POST['courses']
                courses_pks = courses_pks_str.split(',')
                selected_student.course_set.clear()

                # only needed if course set is non empty
                if not courses_pks_str == '':
                    for course_pk in courses_pks:
                        course = Course.objects.get(pk=course_pk)
                        selected_student.course_set.add(course)
                        course.student_set.add(selected_student)
                        course.save()

                selected_student.save()

                # getting new values
                signed_courses = selected_student.course_set.all()
                unsigned_courses = [course for course in all_courses if course not in signed_courses]
                return render(request, 'special_admin/student_crud/old_student.html', {'student': selected_student,
                                                                                       'signed_courses': signed_courses,
                                                                                       'unsigned_courses': unsigned_courses,
                                                                                       'all_courses': all_courses,
                                                                                       'message': 'updated'})

    if request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            # getting course sets
            signed_courses = selected_student.course_set.all()
            unsigned_courses = [course for course in all_courses if course not in signed_courses]
            return render(request, 'special_admin/student_crud/old_student.html', {'student': selected_student,
                                                                                   'signed_courses': signed_courses,
                                                                                   'unsigned_courses': unsigned_courses,
                                                                                   'all_courses': all_courses})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def courses(request):
    if request.method == 'POST':
        if 'delete' in request.POST:
            delete_course = get_object_or_404(Course, pk=request.POST['delete'])
            delete_course.delete()
            all_courses = Course.objects.all()
            return render(request, 'special_admin/course_crud/courses.html', {'all_courses': all_courses})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            all_courses = Course.objects.all()
            return render(request, 'special_admin/course_crud/courses.html', {'all_courses': all_courses})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def new_course(request):
    all_students = Student.objects.all()

    if request.method == 'POST':
        if 'students' in request.POST:
            try:
                Course.objects.get(code=request.POST['course_code'])
                return render(request, 'special_admin/course_crud/new_course.html', {'all_students': all_students,
                                                                                     'message': 'Course with same Code already Exists'})
            except ObjectDoesNotExist:
                # adding new course
                _new_course = Course(code=request.POST['course_code'], name=request.POST['course_name'])
                _new_course.save()

                students_pks_str = request.POST['students']
                students_pks = students_pks_str.split(',')

                if not students_pks_str == '':
                    for student_pk in students_pks:
                        student = Student.objects.get(pk=student_pk)
                        _new_course.student_set.add(student)
                        student.course_set.add(_new_course)

            # deadline midsem + 60 days from present date time
            # deadline midsem + 120 days from present date time
            present_time = datetime.now()
            plus_two_months = present_time + timedelta(days=60)
            plus_four_months = present_time + timedelta(days=120)

            midsem = Task(tag='Mid semester Exam', detail='',
                          deadline=datetime_helper.simple_representation(plus_two_months))
            endsem = Task(tag='End semester Exam', detail='',
                          deadline=datetime_helper.simple_representation(plus_four_months))
            # saving task
            midsem.save()
            endsem.save()

            # adding to task set of course
            _new_course.task_set.add(midsem)
            _new_course.task_set.add(endsem)

            return PerRedirect(reverse('special_admin:tasks', kwargs={'pk': _new_course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/course_crud/new_course.html', {'all_students': all_students})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def old_course(request, pk):
    all_students = Student.objects.all()
    selected_course = get_object_or_404(Course, pk=pk)

    if request.method == 'POST':
        # delete student
        if 'delete_course' in request.POST:
            selected_course.delete()
            return PerRedirect(reverse('special_admin:courses'))

        # update student
        if 'students' in request.POST:
            try:
                Course.objects.get(code=request.POST['course_code'])

                # The same code exists for some other course
                if request.POST['course_code'] != selected_course.code:
                    # getting student sets
                    signed_students = selected_course.student_set.all()
                    unsigned_students = [student for student in all_students if student not in signed_students]
                    return render(request, 'special_admin/course_crud/old_course.html', {'course': selected_course,
                                                                                         'signed_students': signed_students,
                                                                                         'unsigned_students': unsigned_students,
                                                                                         'all_students': all_students,
                                                                                         'message': 'Course with same Code already Exists'})
                # exists but for the same course
                else:
                    # clearing previous student set
                    selected_course.student_set.clear()
                    # removing the current course from course sets of all students
                    for student in all_students:
                        student.course_set.remove(selected_course)

                    # update basic info
                    selected_course.name = request.POST['course_name']
                    selected_course.code = request.POST['course_code']

                    # update student set
                    students_pks_str = request.POST['students']
                    students_pks = students_pks_str.split(',')

                    # only needed if student set is non empty
                    if not students_pks_str == '':
                        for student_pk in students_pks:
                            student = Student.objects.get(pk=student_pk)
                            selected_course.student_set.add(student)
                            student.course_set.add(selected_course)
                            student.save()

                    # saving the entry
                    selected_course.save()

                    # getting new values
                    signed_students = selected_course.student_set.all()
                    unsigned_students = [student for student in all_students if student not in signed_students]
                    return render(request, 'special_admin/course_crud/old_course.html', {'course': selected_course,
                                                                                         'signed_students': signed_students,
                                                                                         'unsigned_students': unsigned_students,
                                                                                         'all_students': all_students,
                                                                                         'message': 'updated'})

            except ObjectDoesNotExist:
                # clearing previous student set
                selected_course.student_set.clear()
                # removing the current course from course sets of all students
                for student in all_students:
                    student.course_set.remove(selected_course)

                # update basic info
                selected_course.name = request.POST['course_name']
                selected_course.code = request.POST['course_code']

                # update student set
                students_pks_str = request.POST['students']
                students_pks = students_pks_str.split(',')

                # only needed if student set is non empty
                if not students_pks_str == '':
                    for student_pk in students_pks:
                        student = Student.objects.get(pk=student_pk)
                        selected_course.student_set.add(student)
                        student.course_set.add(selected_course)
                        student.save()

                # saving the entry
                selected_course.save()

                # getting new values
                signed_students = selected_course.student_set.all()
                unsigned_students = [student for student in all_students if student not in signed_students]
                return render(request, 'special_admin/course_crud/old_course.html', {'course': selected_course,
                                                                                     'signed_students': signed_students,
                                                                                     'unsigned_students': unsigned_students,
                                                                                     'all_students': all_students,
                                                                                     'message': 'updated'})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            # getting student sets
            signed_students = selected_course.student_set.all()
            unsigned_students = [student for student in all_students if student not in signed_students]
            return render(request, 'special_admin/course_crud/old_course.html', {'course': selected_course,
                                                                                 'signed_students': signed_students,
                                                                                 'unsigned_students': unsigned_students,
                                                                                 'all_students': all_students})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


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

            return render(request, 'special_admin/task_crud/tasks.html', {'course': selected_course,
                                                                          'all_course': all_courses,
                                                                          'finished_tasks': finished_tasks,
                                                                          'remaining_tasks': remaining_tasks})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/task_crud/tasks.html', {'course': selected_course,
                                                                          'all_course': all_courses,
                                                                          'finished_tasks': finished_tasks,
                                                                          'remaining_tasks': remaining_tasks})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def new_task(request, pk):
    course = get_object_or_404(Course, pk=pk)

    if request.method == 'POST':
        if 'task_tag' in request.POST:
            try:
                Task.objects.get(tag=request.POST['task_tag'])
                return render(request, 'special_admin/task_crud/new_task.html', {'course': course,
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

                return PerRedirect(reverse('special_admin:tasks', kwargs={'pk': course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/task_crud/new_task.html', {'course': course})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


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
            return PerRedirect(reverse('special_admin:tasks', kwargs={'pk': selected_course.pk}))

        if 'task_tag' in request.POST:
            try:
                Task.objects.get(tag=request.POST['task_tag'])

                # the same tag exists for some other event
                if request.POST['task_tag'] != selected_task.tag:
                    # special admin logged in
                    if request.session.get(sk.special_admin_logged, False):
                        return render(request, 'special_admin/task_crud/old_task.html', {'task': selected_task,
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

                    return render(request, 'special_admin/task_crud/old_task.html', {'task': selected_task,
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

                return render(request, 'special_admin/task_crud/old_task.html', {'task': selected_task,
                                                                                 'year': date[2],
                                                                                 'month': date[1],
                                                                                 'day': date[0],
                                                                                 'hour': time[0],
                                                                                 'minute': time[1],
                                                                                 'course': selected_course,
                                                                                 'message': 'updated'})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/task_crud/old_task.html', {'task': selected_task,
                                                                             'year': date[2],
                                                                             'month': date[1],
                                                                             'day': date[0],
                                                                             'hour': time[0],
                                                                             'minute': time[1],
                                                                             'course': selected_course})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


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
            return render(request, 'special_admin/fb_form_crud/fb_forms.html', {'course': selected_course,
                                                                                'all_course': all_courses,
                                                                                'finished_fb_forms': finished_fb_forms,
                                                                                'remaining_fb_forms': remaining_fb_forms})

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/fb_form_crud/fb_forms.html', {'course': selected_course,
                                                                                'all_course': all_courses,
                                                                                'finished_fb_forms': finished_fb_forms,
                                                                                'remaining_fb_forms': remaining_fb_forms})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def new_feedback_form(request, pk):
    course = get_object_or_404(Course, pk=pk)

    if request.method == 'POST':
        if 'fb_form_name' in request.POST:
            try:
                FeedBackForm.objects.get(name=request.POST['fb_form_name'])
                return render(request, 'special_admin/fb_form_crud/new_fb_form.html', {'course': course,
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

                return PerRedirect(reverse('special_admin:feedback_forms', kwargs={'pk': course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/fb_form_crud/new_fb_form.html', {'course': course,
                                                                                   'delimiter': fbs.form_delimiter})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def old_feedback_form(request, pk):
    selected_fb_form = get_object_or_404(FeedBackForm, pk=pk)
    selected_course = Course.objects.get(pk=selected_fb_form.course_id)
    date_time_parts = selected_fb_form.deadline.split(" ")
    date = date_time_parts[0].split("/")
    time = date_time_parts[1].split(":")
    question_set = selected_fb_form.question_set.split(fbs.form_delimiter)

    if request.method == 'POST':
        # delete student
        if 'delete_fb_form' in request.POST:
            selected_fb_form.delete()
            return PerRedirect(reverse('special_admin:feedback_forms', kwargs={'pk': selected_course.pk}))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/fb_form_crud/old_fb_form.html', {'fb_form': selected_fb_form,
                                                                                   'year': date[2],
                                                                                   'month': date[1],
                                                                                   'day': date[0],
                                                                                   'hour': time[0],
                                                                                   'minute': time[1],
                                                                                   'course': selected_course,
                                                                                   'question_set': question_set})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def tasks_redirect(request):
    # special admin logged in
    if request.session.get(sk.special_admin_logged, False):
        all_courses = Course.objects.all()
        if all_courses:
            return PerRedirect(reverse('special_admin:tasks', kwargs={'pk': all_courses[0].pk}))
        else:
            return render(request, 'special_admin/home.html')
    # NOT logged in
    else:
        return render(request, 'special_admin/login.html')


def fb_forms_redirect(request):
    # special admin logged in
    if request.session.get(sk.special_admin_logged, False):
        all_courses = Course.objects.all()
        if all_courses:
            return PerRedirect(reverse('special_admin:feedback_forms', kwargs={'pk': all_courses[0].pk}))
        else:
            return render(request, 'special_admin/home.html')
    # NOT logged in
    else:
        return render(request, 'special_admin/login.html')
