from django.shortcuts import render
from django.http import HttpResponse

import json
import psycopg2

datasource = ""

# Create your views here.
def index(request):
    return HttpResponse("Quartet")

def execute(request):
    data = json.loads(request.body)
    sql= data["sql"]

    print("Execute: "+sql)
    
    cur = connection.cursor()

    cur.execute(sql)
    
    row = cur.fetchone()

    result= ""
    while row is not None:
        result= result+ str(row)+"\n"
        row = cur.fetchone()
    
    print("Query returned: %s" % ( result ))

    cur.close()

    return HttpResponse(result)

def datasource(request):
    print ("Raw Data: %s " %request.body  )
    data = json.loads(request.body)
    print("host="+data["ip"]+" port="+data["port"]+" dbname="+data["dbname"]+" user="+data["user"]+" password="+data["password"])

    global connection
    connection = psycopg2.connect("host="+data["ip"]+" port="+data["port"]+" dbname="+data["dbname"]+" user="+data["user"]+" password="+data["password"])
    connection.set_session(autocommit=True)

    return HttpResponse("Datasource")


def index(request):
    return render(request, 'index.html')