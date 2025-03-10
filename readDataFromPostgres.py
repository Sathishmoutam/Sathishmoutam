import pandas as pd
from sqlalchemy import create_engine

username = 'postgres'
password = 'postgres'
host = 'localhost'
port = '5432' 
database = 'postgres'
table_name = 'users'

engine_string = f"postgresql+psycopg2://{username}:{password}@{host}:{port}/{database}"
engine = create_engine(engine_string)
df = pd.read_sql_table(table_name, engine)
print(df.head())    



# create function that reads data from postgres and returns a dataframe










