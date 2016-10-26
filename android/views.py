from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render
from special_admin.models import Student


@csrf_exempt
def student_login(request):
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


@csrf_exempt
def student_password_change(request):
    if request.method == 'POST':
        if 'user_name' in request.POST and 'old_password' in request.POST and 'new_password' in request.POST:
            try:
                student = Student.objects.get(user_name=request.POST['user_name'], password=request.POST['old_password'])
                student.password = request.POST['new_password']
                student.save()
                return HttpResponse("1")
            except ObjectDoesNotExist:
                return HttpResponse("0")
        else:
            return HttpResponse("0")
    else:
        return HttpResponse("0")
