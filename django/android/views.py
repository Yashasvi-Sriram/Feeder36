from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render
from special_admin.models import Student, FeedBackForm, FeedbackResponse
import json


@csrf_exempt
def student_login(request):
    if request.META.get('HTTP_USER_AGENT', '').lower().find("android") > 0:
        if request.method == 'POST':
            if 'user_name' in request.POST and 'password' in request.POST:
                try:
                    Student.objects.get(user_name=request.POST['user_name'], password=request.POST['password'])
                    return HttpResponse("1")
                except ObjectDoesNotExist:
                    return HttpResponse("0")
            else:
                return HttpResponse("0")
        else:
            return HttpResponse("0")
    else:
        return HttpResponse("0")


@csrf_exempt
def student_password_change(request):
    if request.META.get('HTTP_USER_AGENT', '').lower().find("android") > 0:
        if request.method == 'POST':
            if 'user_name' in request.POST and 'old_password' in request.POST and 'new_password' in request.POST:
                try:
                    student = Student.objects.get(user_name=request.POST['user_name'],
                                                  password=request.POST['old_password'])
                    student.password = request.POST['new_password']
                    student.save()
                    return HttpResponse("1")
                except ObjectDoesNotExist:
                    return HttpResponse("0")
            else:
                return HttpResponse("0")
        else:
            return HttpResponse("0")
    else:
        return HttpResponse("0")


@csrf_exempt
def student_sync(request):
    if request.META.get('HTTP_USER_AGENT', '').lower().find("android") > 0:
        if request.method == 'POST':
            if 'user_name' in request.POST and 'password' in request.POST:
                try:
                    student = Student.objects.get(user_name=request.POST['user_name'],
                                                  password=request.POST['password'])
                    courses = student.course_set.all()

                    # making a list of dictionaries
                    course_dict_list = []
                    task_dict_list = []
                    fb_forms_dict_list = []
                    fb_responses_dict_list = []

                    # inserting into both dictionaries
                    for course in courses:

                        course_dict = {'django_pk': course.pk,
                                       'code': course.code,
                                       'name': course.name
                                       }
                        course_dict_list.append(course_dict)
                        # inserting tasks
                        for task in course.task_set.all():
                            task_dict = {'django_pk': task.pk,
                                         'course_pk': task.course_id,
                                         'tag': task.tag,
                                         'deadline': task.deadline,
                                         'detail': task.detail
                                         }
                            task_dict_list.append(task_dict)
                        # inserting fb forms
                        for fb_form in course.feedbackform_set.all():
                            fb_form_dict = {'django_pk': fb_form.pk,
                                            'course_pk': fb_form.course_id,
                                            'name': fb_form.name,
                                            'deadline': fb_form.deadline,
                                            'question_set': fb_form.question_set
                                            }
                            fb_forms_dict_list.append(fb_form_dict)
                            try:
                                # Response submitted
                                fb_response = fb_form.feedbackresponse_set.get(student=student)
                                fb_response_dict = {'feedback_form_pk': fb_form.pk,
                                                    'comment': fb_response.comment,
                                                    'answer_set': fb_response.answer_set}
                                fb_responses_dict_list.append(fb_response_dict)
                            except ObjectDoesNotExist:
                                # Response not yet submitted
                                pass

                    # Making an dictionary of lists
                    # 1. Courses 2. Tasks 3. FeedbackForms
                    courses_tasks_dict = {'courses': course_dict_list, 'tasks': task_dict_list,
                                          'feedback_forms': fb_forms_dict_list,
                                          'feedback_responses': fb_responses_dict_list}
                    return HttpResponse(json.dumps(courses_tasks_dict), content_type='application/json')
                except ObjectDoesNotExist:
                    return HttpResponse("0")
            else:
                return HttpResponse("0")
        else:
            return HttpResponse("0")
    else:
        return HttpResponse("0")


@csrf_exempt
def student_submit(request):
    if request.META.get('HTTP_USER_AGENT', '').lower().find("android") > 0:
        if request.method == 'POST':
            if 'user_name' in request.POST and 'password' in request.POST:
                if Student.objects.filter(user_name=request.POST['user_name'],
                                          password=request.POST['password']).exists():
                    student = Student.objects.get(user_name=request.POST['user_name'],
                                                  password=request.POST['password'])
                    responses_json = json.loads(request.POST['feedback_responses'])
                    successful_response_submit_list = []
                    for response_json in responses_json:
                        fb_form = FeedBackForm.objects.get(pk=response_json['feedback_form_pk'])
                        fb_response, created = FeedbackResponse.objects.get_or_create(
                            answer_set=response_json['answer_set'],
                            comment=response_json['comment'])
                        if created:
                            fb_form.feedbackresponse_set.add(fb_response)
                            student.feedbackresponse_set.add(fb_response)
                            successful_response_submit_list.append(response_json['feedback_form_pk'])

                    return HttpResponse(json.dumps(successful_response_submit_list))
                else:
                    return HttpResponse("0")
            else:
                return HttpResponse("0")
        else:
            return HttpResponse("0")
    else:
        return HttpResponse("0")
