/**********************************************************************/
* Name    : DWH.PUBLIC.SF_GET_CASE_4_PERSON
*
* GITHUB File Name : SF_GET_CASE_4_PERSON.sql
*
* Version :        1.0
*
* Details: <Short description about the table>
*
* Edit History :
*
*  Name               Ver     Date      JIRA Ticket      Comment
*  ------------------ ---    --------   ------------- --------------------
*
*  Tharun            1.0     02/25/25                    Initial
/**********************************************************************/

CREATE OR REPLACE FUNCTION SF_GET_CASE_4_PERSON("P_PERSON_ID" NUMBER(38,0), "P_DATE" DATE DEFAULT CURRENT_DATE(), "P_CURR_LAST_FLAG" VARCHAR DEFAULT 'L', "P_CHILD_ONLY_FLAG" VARCHAR DEFAULT 'N', "P_CASE_TYPE_FLAG" VARCHAR DEFAULT '1')
RETURNS NUMBER(38,0)
LANGUAGE SQL
AS '
    Select MAX(CASE 
                WHEN UPPER(P_Curr_Last_Flag) = ''C'' THEN (CASE 
                                                            WHEN NVL(P_DATE,CURRENT_DATE) BETWEEN START_DATE AND NVL(END_DATE,CURRENT_DATE) THEN CAS_CAS_ID
                                                        END)
                WHEN UPPER(P_Curr_Last_Flag) = ''L'' THEN CAS_CAS_ID
                ELSE NULL
            END)
      From FACT_PERSON_ORG_INVOLVEMENT poi --T_Person_Org_Involvement
     Where poi.person_person_id = P_Person_Id
       And poi.Cas_cas_id is not null
       And ( ( P_Case_Type_Flag = ''1''
               and
               sf_get_case_type(poi.cas_cas_id) Not in
               ( ''150282'', -- Adoption Legalized with Subsidy
                 ''153053'', -- Adoption Legalized without Subsidy
                 ''153052'', -- Guardianship Subsidy
                 ''152792''  -- Institutional Abuse
               )
             )
             or
             ( P_Case_Type_Flag = ''2''
               and
               sf_get_case_type(poi.cas_cas_id) in
               ( ''150282'', -- Adoption Legalized with Subsidy
                 ''153053'', -- Adoption Legalized without Subsidy
                 ''153052''  -- Guardianship Subsidy
               )
             )
             or
             P_Case_Type_Flag = ''3''
             or -- Ref. ''RI-40'' Added by VHari on 3/27/2018
             ( P_Case_Type_Flag = ''4''
               and
               sf_get_case_type(poi.cas_cas_id) in
               ( ''103412''   -- Adoption
               )
             )
           )
       And ( ( UPPER(P_Child_Only_Flag) = ''N''
               and
               sf_get_poi_roles(poi.poi_id, ''AV_ID'') in
               ( ''103313'', -- Consumer Adult
                 ''103312''  -- Consumer Child
               )
             )
             or
             ( UPPER(P_Child_Only_Flag) = ''Y''
               and
               sf_get_poi_roles(poi.poi_id, ''AV_ID'') = ''103312''  -- Consumer Child
             )
           )
       and DATE_TRUNC(''DAY'',poi.start_date) <= nvl(P_DATE, CURRENT_DATE)
     Order by poi.end_date   Desc,
              poi.start_date
';