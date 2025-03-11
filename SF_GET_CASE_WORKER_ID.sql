/**********************************************************************/
* Name    : DWH.PUBLIC.SF_GET_CASE_WORKER_ID
*
* GITHUB File Name : SF_GET_CASE_WORKER_ID.sql
*
* Version :        2.0
*
* Details: <Short description about the table>
*
* Edit History :
*
*  Name               Ver     Date      JIRA Ticket      Comment
*  ------------------ ---    --------   ------------- --------------------
*
*  sathish            2.0    02/25/25                    Initial
/**********************************************************************/


CREATE OR REPLACE FUNCTION SF_GET_CASE_WORKER_ID("CAS_ID" NUMBER(38,0), "S_DATE" DATE DEFAULT CURRENT_DATE())
RETURNS NUMBER(38,0)
LANGUAGE SQL
AS '
 Select 
 MAX(sp_sp_id)
 From  FACT_STAFF_PERSON_OFFICE_ASSIGNS
Where  assignment_type_AV_ID = ''97982'' --primary
And    cas_cas_id =  CAS_ID
And    assignment_start_date <= nvl(S_DATE, CURRENT_DATE)

';



-- Create a Snowflake function named case_id_details that returns a table with case_id, count of case_id, and sum of case_id from case_table, grouped by case_id

































