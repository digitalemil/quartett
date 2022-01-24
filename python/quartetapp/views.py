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
    print("Connection: "+str(connection))
    
    cur = connection.cursor()

    cur.execute(sql)
  
    result= ""
    
    print("Row count: "+str(cur.rowcount))

    try: 
        if cur.rowcount > 0:
            row = cur.fetchone()

            while row is not None:
                result= result+ str(row)+"\n"
                row = cur.fetchone()
    except (RuntimeError, TypeError, NameError, psycopg2.ProgrammingError):
        pass
    
    result= result+ cur.statusmessage

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