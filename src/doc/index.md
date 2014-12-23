FORGEStore Web Service
==========

FORGEStore is a RESTful marketplace service for a FORGEBox installation.
FORGEStore contains shared widgets, FORGEBox services, FIRE adapters and shared interactive courses.

A running instance can be found at http://www.forgestore.eu


REST API
--------

All FORGEStore API,  Produces("application/json") and Consumes("application/json") except some POSTs that Consumes("multipart/form-data")
All requests should be to the repo of the webservice. 
For example if you have context path /fsapi for the webservice then put the full path as /fsapi/services/api/repo

The following examples are against the  API endpoind
http://localhost:13000/fsapi/services/api/repo/


Authenticate, Create Session
----------------------------

    curl -v -H "Content-Type: application/json" -X POST --data '{"username":"admin", "password":"changeme"}' http://localhost:13000/fsapi/services/api/repo/sessions

Response

    {
        "id": 24,
        "username": "admin",
        "password": "",
        "sessionId": "44d9e657-9c8a-423d-9015-e7cb81fa0257",
        "user": {
            "id": 1,
            "organization": "",
            "name": "FStore Administrator",
            "email": "",
            "username": "admin",
            "password": "TLnIqASP0CKUR3/LGkEZGg==",
            "role": "ROLE_ADMIN",
            "active": true
        }
    }

In the following examples JSESSIONID cookie value is equal to the sessionId (and JSESSIONID given from server). Must be present for authenticated requests


Categories
----------
GET all categories

    curl -v -H "Content-Type: application/json" http://localhost:13000/fsapi/services/api/repo/categories

Response sample:

    [{ {{
        "id": 2,
        "name": "Multimedia",
        "widgetscount": 0,
        "coursescount": 0,
        "fireadapterscount": 0,
        "productsCount": 0
    },{"id":3,"name":"Remote access","widgetscount":4,"courses .....


management of categories only if authorized, only admin role


GET all categories

    curl -v -H "Content-Type: application/json" http://localhost:13000/fsapi/services/api/repo/admin/categories

Add a new category (if authorized, only admin role)

    curl -v -H "Content-Type: application/json" -X POST --data '{"name":"NewCateg"}' --cookie "JSESSIONID=5dfe834e-6024-4806-9caf-7bbbd73c68f2"  http://localhost:13000/fsapi/services/api/repo/admin/categories

JSESSIONID is the same value returned from sessionId

Response sample:

    {
        "id": 7,
        "name": "NewCateg",
        "widgetscount": 0,
        "coursescount": 0,
        "fireadapterscount": 0,
        "productsCount": 0
    }

Update a category(if authorized, only admin role)

    curl -H "Content-Type: application/json" -X PUT --data '{"id":"10", "name":"NewCategNAMEX"}'  http://localhost:13000/fsapi/services/api/repo/admin/categories/10

Response sample:

    {
        "id": 10,
        "name": "NewCategNAMEX",
        "widgetscount": 0,
        "coursescount": 0,
        "fireadapterscount": 0,
        "productsCount": 0
    }


Users
-----
Getting all users (if authorized, only admin role)

    curl -v -H "Content-Type: application/json" http://localhost:13000/fsapi/services/api/repo/users/

Widgets
-------
### Get all widgets

    curl -v -H "Content-Type: application/json" http://localhost:13000/fsapi/services/api/repo/widgets/


### Get a widget

    curl -v -H "Content-Type: application/json" http://localhost:13000/fsapi/services/api/repo/widgets/1
	
Response:

	{
	    "id": 1,
	    "owner": {
	        "id": 1,
	        "organization": "University of Patras",
	        "name": "FStore Administrator",
	        "email": "tranoris@ece.upatras.gr",
	        "username": "admin",
	        "role": "ROLE_ADMIN",
	        "active": true
	    },
	    "uuid": "3a64d22c-223f-436b-8179-4f65d81924b1",
	    "name": "ssh2web",
	    "iconsrc": "http://www.forgestore.eu:8080/baker/services/api/repo/images/3a64d22c-223f-436b-8179-4f65d81924b1/ssh2web.PNG",
	    "shortDescription": "ssh web proxy service hosted by Univ. of Patras",
	    "longDescription": "This widget provides remote access to machines through an ssh proxy. Users can use ssh access normally without need to install any tool. This service is hosted by Univ. of Patras",
	    "version": "1.0.2",
	    "packageLocation": "http://www.forgestore.eu:8080/baker/services/api/repo/packages/3a64d22c-223f-436b-8179-4f65d81924b1/unknown",
	    "dateCreated": 1414258897000,
	    "dateUpdated": 1414258897000,
	    "categories": [{
	        "id": 4,
	        "name": "Course support",
	        "widgetscount": 5,
	        "coursescount": 0,
	        "fireadapterscount": 4,
	        "productsCount": 9
	    }, {
	        "id": 8,
	        "name": "Experiment support",
	        "widgetscount": 5,
	        "coursescount": 0,
	        "fireadapterscount": 2,
	        "productsCount": 7
	    }, {
	        "id": 3,
	        "name": "Remote access",
	        "widgetscount": 4,
	        "coursescount": 0,
	        "fireadapterscount": 3,
	        "productsCount": 7
	    }],
	    "extensions": [],
	    "screenshots": "http://www.forgestore.eu:8080/baker/services/api/repo/images/3a64d22c-223f-436b-8179-4f65d81924b1/shot1_ss2webcapture1.png,
	    http: //www.forgestore.eu:8080/baker/services/api/repo/images/3a64d22c-223f-436b-8179-4f65d81924b1/shot2_ss2webcapture2.png,
	        http: //www.forgestore.eu:8080/baker/services/api/repo/images/3a64d22c-223f-436b-8179-4f65d81924b1/shot3_ss2webcapture3.png",
	        "url": "http://www.forgebox.eu:8080/ssh2web"
	}

## Management of widgets only if authorized

### Add widget 
(multipart/form-data) with params:
widget name, prodname="MyAPI Widget" 
endpoint url that services the widget, url="http://mywidget.example.com/endpoint" 
Categories that belongs, comma separated category IDs, categories=3,1 
Short teaser, shortDescription="shortde" 
Description of widget, longDescription="a longDescription" 
A Version, version="1.2.3a" 
A Widget Logo, prodIcon=@course_smallico.PNG 
The session auth cookie, cookie "JSESSIONID=2ba937c5-e93e-4c68-8ff5-99dea2d40bb7"  
The endpoint to call, http://localhost:13000/fsapi/services/api/repo/admin/widgets
Example:

    curl -v -include --form prodname="MyAPI Widget" --form url="http://mywidget.example.com/endpoint" --form categories=3,1 --form shortDescription="shortde" --form longDescription="a longDescription" --form version="1.2.3a" --form prodIcon=@course_smallico.PNG --cookie "JSESSIONID=2ba937c5-e93e-4c68-8ff5-99dea2d40bb7"  http://localhost:13000/fsapi/services/api/repo/admin/widgets

Response:

    {
        "id": 19,
        "owner": {
            "id": 1,
            "organization": "",
            "name": "FStore Administrator",
            "email": "",
            "username": "admin",
            "role": "ROLE_ADMIN",
            "active": true
        },
        "uuid": "f25f1409-14e5-4002-9fea-afd983a59b10",
        "name": "MyAPI Widget",
        "iconsrc": "http://localhost:13000/fsapi/services/api/repo/images/f25f1409-14e5-4002-9fea-afd983a59b10/course_smallico.PNG",
        "shortDescription": "shortde",
        "longDescription": "a longDescription",
        "version": "1.2.3a",
        "packageLocation": null,
        "dateCreated": 1417988323618,
        "dateUpdated": 1417988323618,
        "categories": [{
            "id": 3,
            "name": "Experimentation",
            "widgetscount": 8,
            "coursescount": 0,
            "fireadapterscount": 0,
            "productsCount": 8
        }, {
            "id": 1,
            "name": "None",
            "widgetscount": 9,
            "coursescount": 0,
            "fireadapterscount": 0,
            "productsCount": 9
        }],
        "extensions": [],
        "screenshots": "",
        "url": null
    }



### Add widget in a package (.tar.gz). The widget can be hosted in the service under a static url. An index.html must be present
(multipart/form-data)

(multipart/form-data) with params:
widget name, prodname="MyAPI Widget" 
endpoint url that services the widget, url="http://mywidget.example.com/endpoint" 
Categories that belongs, comma separated category IDs, categories=3,1 
Short teaser, shortDescription="shortde" 
Description of widget, longDescription="a longDescription" 
A Version, version="1.2.3a" 
A Widget Logo, prodIcon=@course_smallico.PNG 
The packaged widget content, prodFile=@mywidget.tar.gz
The session auth cookie, cookie "JSESSIONID=2ba937c5-e93e-4c68-8ff5-99dea2d40bb7"  
The endpoint to call, http://localhost:13000/fsapi/services/api/repo/admin/widgets
Example:

	curl -v -include --form prodname="MyAPI Widget" --form categories=3,1 --form shortDescription="shortde" --form longDescription="a longDescription" --form version="1.2.3a" --form prodIcon=@course_smallico.PNG --cookie "JSESSIONID=be5c5693-def5-4c64-8460-3044856fe1f9" --form prodFile=@mywidget.tar.gz  http://localhost:13000/fsapi/services/api/repo/admin/widgets/packaged


Response:
	{
	    "id": 25,
	    "owner": {
	        "id": 1,
	        "organization": "",
	        "name": "FStore Administrator",
	        "email": "",
	        "username": "admin",
	        "role": "ROLE_ADMIN",
	        "active": true
	    },
	    "uuid": "ccd0153b-e71f-4514-b477-78d1a71bb6eb",
	    "name": "MyAPI Widget",
	    "iconsrc": "http://localhost:13000/fsapi/services/api/repo/images/ccd0153b-e71f-4514-b477-78d1a71bb6eb/course_smallico.PNG",
	    "shortDescription": "shortde",
	    "longDescription": "a longDescription",
	    "version": "1.2.3a",
	    "packageLocation": "http://localhost:13000/fsapi/services/api/repo/packages/ccd0153b-e71f-4514-b477-78d1a71bb6eb/mywidget.tar.gz",
	    "dateCreated": 1419343819000,
	    "dateUpdated": 1419343819000,
	    "categories": [{
	        "id": 3,
	        "name": "Experimentation",
	        "widgetscount": 16,
	        "coursescount": 0,
	        "fireadapterscount": 0,
	        "productsCount": 16
	    }, {
	        "id": 1,
	        "name": "None",
	        "widgetscount": 17,
	        "coursescount": 0,
	        "fireadapterscount": 0,
	        "productsCount": 17
	    }],
	    "extensions": [],
	    "screenshots": "",
	    "url": "http://localhost:13000/static/ccd0153b-e71f-4514-b477-78d1a71bb6eb/"
    }

### Update a Widget

    curl -v -X PUT -H "Content-Type: multipart/form-data" -include --form userid=1 --form prodname="MyAPI WidgetNew name" --form categories=3,1 --form shortDescription="shortde" --form longDescription="a longDescription" --form version="1.2.3a" --form prodIcon=@course_smallico.PNG --cookie "JSESSIONID=928f32fb-16e4-4b30-90f9-50235f9c197f"  http://localhost:13000/fsapi/services/api/repo/admin/widgets/19

Response:

    {
        "id": 19,
        "owner": {
            "id": 1,
            "organization": "",
            "name": "FStore Administrator",
            "email": "",
            "username": "admin",
            "role": "ROLE_ADMIN",
            "active": true
        },
        "uuid": "255148c7-5562-4c51-a009-81f2c0d81efe",
        "name": "MyAPI WidgetNew name",
        "iconsrc": "http://localhost:13000/fsapi/services/api/repo/images/null/course_smallico.PNG",
        "shortDescription": "shortde",
        "longDescription": "a longDescription",
        "version": "1.2.3a",
        "packageLocation": null,
        "dateCreated": 1417988651000,
        "dateUpdated": 1417988946320,
        "categories": [{
            "id": 3,
            "name": "Experimentation",
            "widgetscount": 9,
            "coursescount": 0,
            "fireadapterscount": 0,
            "productsCount": 9
        }, {
            "id": 1,
            "name": "None",
            "widgetscount": 10,
            "coursescount": 0,
            "fireadapterscount": 0,
            "productsCount": 10
        }],
        "extensions": [],
        "screenshots": "",
        "url": null
    }


Delete Widget (example with id=19):

    curl -v -X DELETE --cookie "JSESSIONID=928f32fb-16e4-4b30-90f9-50235f9c197f" http://localhost:13000/fsapi/services/api/repo/admin/widgets/19























