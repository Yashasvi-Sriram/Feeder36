from django.conf.urls import url,include
from django.contrib import admin

urlpatterns = [
    # localhost:8036/admin/
    url(r'^admin/', admin.site.urls),
    # localhost:8036/special_admin/
    url(r'^special_admin/', include('special_admin.urls')),
]
