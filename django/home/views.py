from django.http import HttpResponsePermanentRedirect as PerRedirect
from django.http import HttpResponseRedirect
from django.shortcuts import render, get_object_or_404
from django.urls import reverse


def home(request):
    return render(request, 'home/home.html')
