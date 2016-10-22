from django.shortcuts import render
from . import models
from .session_keys import SessionKeys as sk


# Create your views here.
def home(request):
    # POST request
    if request.method == 'POST':

        # Login request
        if 'special_admin_email' in request.POST and 'special_admin_password' in request.POST:
            # Validating SuperUser credentials from the fields statically typed in code
            error_set = {}
            if request.POST['special_admin_email'] != models.Super.email:
                error_set['email_incorrect'] = 'Email incorrect'
            if request.POST['special_admin_password'] != models.Super.password:
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
