from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.http import HttpResponsePermanentRedirect as PerRedirect

from .models import Student, Course, SpecialAdmin
from .session_keys import SessionKeys as sk


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
            delete_student = Student.objects.get(pk=request.POST['delete'])
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


def old(request, pk):
    all_courses = Course.objects.all()
    student_selected = get_object_or_404(Student, pk=pk)

    if request.method == 'POST':
        # delete student
        if 'delete_student' in request.POST:
            student_selected.delete()
            return PerRedirect(reverse('special_admin:students'))

        # update student
        if 'courses' in request.POST:
            # update basic info
            student_selected.name = request.POST['student_name']
            student_selected.user_name = request.POST['student_user_name']
            student_selected.save()

            # update course set
            courses_pks_str = request.POST['courses']
            courses_pks = courses_pks_str.split(',')
            student_selected.course_set.clear()

            # only needed if course set is non empty
            if not courses_pks_str == '':
                for course_pk in courses_pks:
                    course = Course.objects.get(pk=course_pk)
                    student_selected.course_set.add(course)

            # getting new values
            signed_courses = student_selected.course_set.all()
            unsigned_courses = [course for course in all_courses if course not in signed_courses]
            return render(request, 'special_admin/student_crud/old_student.html', {'student': student_selected,
                                                                                   'signed_courses': signed_courses,
                                                                                   'unsigned_courses': unsigned_courses,
                                                                                   'all_courses': all_courses,
                                                                                   'message': 'updated'})

    if request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            # getting course sets
            signed_courses = student_selected.course_set.all()
            unsigned_courses = [course for course in all_courses if course not in signed_courses]
            return render(request, 'special_admin/student_crud/old_student.html', {'student': student_selected,
                                                                                   'signed_courses': signed_courses,
                                                                                   'unsigned_courses': unsigned_courses,
                                                                                   'all_courses': all_courses})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')


def new(request):
    all_courses = Course.objects.all()

    if request.method == 'POST':
        if 'courses' in request.POST:
            new_student = Student(name=request.POST['student_name'], user_name=request.POST['student_user_name'])
            courses_pks_str = request.POST['courses']
            courses_pks = courses_pks_str.split(',')
            new_student.save()

            if not courses_pks_str == '':
                for course_pk in courses_pks:
                    course = Course.objects.get(pk=course_pk)
                    new_student.course_set.add(course)

        return PerRedirect(reverse('special_admin:students'))

    elif request.method == 'GET':
        # special admin logged in
        if request.session.get(sk.special_admin_logged, False):
            return render(request, 'special_admin/student_crud/new_student.html', {'all_courses': all_courses})
        # NOT logged in
        else:
            return render(request, 'special_admin/login.html')
