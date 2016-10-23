from django.shortcuts import render, redirect, get_object_or_404
from django.urls import reverse
from django.http import HttpResponsePermanentRedirect as PerRedirect, HttpResponse

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

        return PerRedirect(reverse('special_admin:courses'))

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

    if request.method == 'GET':
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
