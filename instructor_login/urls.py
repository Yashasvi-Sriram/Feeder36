from django.conf.urls import url
from . import views

app_name = 'instructor_login'

urlpatterns = [
    url(r'^login/$', views.login , name ='login'),
    url(r'^auth/$', views.auth_view , name ='auth_view'),
    #you need to modify this#add variable#restrict regex
    url(r'^loggedin/$', views.loggedin, name='loggedin'),
    url(r'^logout/$', views.logout, name='logout'),
    url(r'^invalid/$', views.invalid, name='invalid'),
    url(r'^register/$', views.register, name='register'),
    url(r'^register_success/$', views.register_success, name='register_success'),
    url(r'^register_failure/$', views.register_failure, name='register_failure'),
]