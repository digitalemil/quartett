from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('execute', views.execute, name='execute'),
    path('datasource', views.datasource, name='datasource'),
]