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

in the following example JSESSIONID cookie value is equal to the sessionId (and JSESSIONID given from server). Must be present for authenticated requests


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

## Management of widgets only if authorized

### Add widget 
(multipart/form-data)
curl -v -include --form prodname="MyAPI Widget" --form categories=3,1 --form shortDescription="shortde" --form longDescription="a longDescription" --form version="1.2.3a" --form prodIcon=@course_smallico.PNG --cookie "JSESSIONID=2ba937c5-e93e-4c68-8ff5-99dea2d40bb7"  http://localhost:13000/fsapi/services/api/repo/admin/widgets

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























