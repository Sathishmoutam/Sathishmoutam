/**********************************************************************/
* Name    : RAWDATA.PUBLIC.T_ALLOWABLE_VALUES
*
* GITHUB File Name : RAWDATA_T_ALLOWABLE_VALUES.sql
*
* Version :        1.0
*
* Details: <This table stores the descriptions of codes or values used in multiple tables. These are fixed sets of values, like drop-down options or multi-select choices, determined by specific workflows. This table stores the actual values, and their corresponding codes are used in other tables.>
*
* Edit History :
*
*  Name               Ver     Date      JIRA Ticket      Comment
*  ------------------ ---    --------   ------------- --------------------
*
*  Tharun            1.0     06/12/24                   Initial
/**********************************************************************/


create or replace TABLE T_ALLOWABLE_VALUES (
	AV_PK_ID NUMBER(10,0),
	AV_ID VARCHAR(10) NOT NULL,
	AVC_AVC_ID NUMBER(10,0),
	VALUE_CODE VARCHAR(10),
	VALUE_SHORT_DESC VARCHAR(80),
	VALUE_DEFAULT_IND VARCHAR(1),
	VALUE_RULE_NUM NUMBER(38,0),
	VALUE_SORT_ORDER_NUM NUMBER(38,0),
	ADD_TS TIMESTAMP_NTZ(9),
	ADD_USER VARCHAR(20),
	MOD_TS TIMESTAMP_NTZ(9),
	MOD_USER VARCHAR(20),
	USER VARCHAR(20),
	DISCONTINUED_DATE TIMESTAMP_NTZ(9),
	VALUE_LONG_DESC VARCHAR(240),
	MINIMUM_NUM VARCHAR(12),
	MAXIMUM_NUM VARCHAR(12),
	LOADED_DATE TIMESTAMP_NTZ(9),
	CHECKSUM VARCHAR(99),
	DELETED_DATE TIMESTAMP_NTZ(9),
	primary key (AV_ID)
);