from django.conf.urls import url, include
from django.contrib import admin

urlpatterns = [
    # localhost:8036/
    url(r'^', include('home.urls')),
    # localhost:8036/admin/
    url(r'^admin/', admin.site.urls),
    # localhost:8036/special_admin/
    url(r'^special_admin/', include('special_admin.urls')),
    # localhost:8036/android/
    url(r'^android/', include('android.urls')),
    # localhost:8036/instructor_login/
    url(r'^instructor_login/', include('instructor_login.urls')),
    #
    url('', include('social.apps.django_app.urls', namespace='social')),
    url('', include('django.contrib.auth.urls', namespace='auth')),
]
