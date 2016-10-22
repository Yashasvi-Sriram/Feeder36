from django.conf.urls import url
from . import views

app_name = 'special_admin'

urlpatterns = [
    # localhost:8036/special_admin/home/
    url(r'^home/$', views.home, name='home'),
]
