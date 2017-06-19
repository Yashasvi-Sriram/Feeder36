from django.conf.urls import url
from . import views

app_name = 'home'

urlpatterns = [
    # localhost:8036/
    url(r'^$', views.home, name='home'),
]
