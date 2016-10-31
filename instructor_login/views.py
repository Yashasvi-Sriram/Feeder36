from django.shortcuts import render
from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect
from django.http import HttpResponse
from django.template import loader
from django.contrib import auth
from django.contrib.auth.forms import  UserCreationForm
from .forms import MyForm
from django.contrib import messages

#need to check whether to add extra project




dict = {}
# Create your views here.
def login(request):
    #template = loader.get_template('instructor_login/login.html')
    return render(request,'instructor_login/login.html')

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
    return render(request,'instructor_login/loggedin.html',context)

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



