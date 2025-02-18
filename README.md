# Healthcare Tracking

System for tracking doctor visits by patients. It helps to efficiently manage medical records, taking into account the time zones of doctors and avoiding schedule conflicts.



## Functional capabilities:
                                                  Create a visit

Endpoint: POST /api/visits

Request:

```json
{
    "start": "string",
    "end": "string",
    "patientId": "int"
    "doctorId": "int"
}
```
<b>Validation:</b>

The time is transmitted in the doctor's time zone.

One doctor cannot have multiple visits at the same time.

##

                                                 Getting a list of patients
Endpoint: GET /api/visits/patients

Request parameters:

- page - page number.

- size - number of patients per page.

- search - search for a patient by name.

- doctorIds - a comma-separated list of doctor IDs.

<b>Response:</b>
```json
{
   "data": [
       {
           "firstName": "string",
           "lastName": "string",
           "lastVisits": [
               {
                   "start": "string",
                   "end": "string",
                   "doctor": {
                       "firstName": "string",
                       "lastName": "string",
                       "totalPatients": "int"
                   }
               }
           ]
       }
   ],
   "count": "int"
}

```





## Optimization

- Minimized the number of database queries.

- Optimized SQL queries for fast selection.

- Efficient indexes are used to improve performance.

## Test data

- Generated doctors, patients, and visits.

- A doctor can have several patients.

- Patients can visit different doctors.

- The MySQL database dump is added to the repository (src/main/resources/damp.sql)
  
