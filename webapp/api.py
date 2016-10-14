#!/usr/bin/env python3
'''
    api.py
    Matt Javaly & Elliot Mawby
    Updated 30 April 2016
    Adapted from Jeff Ondich, 25 April 2016

    
'''
import sys
import flask
import json
import config
import psycopg2

app = flask.Flask(__name__, template_folder='templates')

def _fetch_all_rows_for_query(query):
    '''
    Returns a list of rows obtained from the books database
    by the specified SQL query. If the query fails for any reason,
    an empty list is returned.
    '''
    try:
        connection = psycopg2.connect(database=config.database, user=config.user, password=config.password)
    except:
        return []

    rows = []
    try:
        cursor = connection.cursor()
        cursor.execute(query)
        rows = cursor.fetchall() # This can be trouble if your query results are really big.
        connection.close()
    except:
        pass

    connection.close()
    return rows

@app.route('/') 
def get_main_page():
    ''' This is the only route intended for human users '''
    return flask.render_template('index.html')

@app.route('/countries_json/')
def get_countries():
    '''
    Returns a list of all the countries in our database, in alphabetical
    order by country_name.
    '''
    query = '''SELECT country_name FROM countries ORDER BY country_name'''

    country_list = []
    for row in _fetch_all_rows_for_query(query):
        country = {'country_name':row[0]}
        country_list.append(country)

    return json.dumps(country_list)

@app.route('/countries/')
def render_countries():
    return flask.render_template('countries_page.html')

@app.route('/statistics/')
def get_statistics():
    '''
    Returns a list of all the statistics in our database, with brief descriptions of each.
    '''

    return flask.render_template('statistics_page.html')

def get_country_weo_by_name(name_of_country):
    '''
    Returns the WEO country code that corresponds to the specified
    country name.
    '''

    query = '''SELECT weo_country_code FROM countries WHERE UPPER(country_name) = UPPER(\'{0}\')'''.format(name_of_country)

    rows = _fetch_all_rows_for_query(query)
    if len(rows) > 0:
        row = rows[0]
        country_code = {'weo':row[0]}
        return country_code

    return {'weo':''}

# @app.route('/<something_1>/')
# def one_argument(something_1):
#     return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
#             Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
#             Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
#             Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
#             Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
#             '''

# @app.route('/<something_1>/<something_2>/')
# def two_arguments(something_1,something_2):
#     return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
#             Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
#             Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
#             Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
#             Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
#             '''

# @app.route('/<something_1>//<something_2>/')
# def no_country(something_1):
#     return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
#             Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
#             Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
#             Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
#             Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
#             '''

# @app.route('//<something_1>/<something_2>/')
# def no_stat(something_1):
#     return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
#             Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
#             Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
#             Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
#             Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
#             '''

# @app.route('/<something_1>/<something_2>//')
# def no_year(something_1):
#     return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
#             Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
#             Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
#             Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
#             Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
#             '''

#our single method for handling all statistic queries
@app.route('/<stat_list>/<country_list>/<year_list>')
def get_country_stat_year(stat_list,country_list,year_list):
    '''
    Returns a list of json responses to the requested queries for 1 or more statistics, 1 or more countries, across 1 or more years.
    The items are separated by '&'s. The method splits the lists on '&'s and then puts the results into the resulting list.
    If any of the syntax is wrong the method returns a usage statement.
    '''
    #keyword functionality to be added later
    # if country_list == 'countries':

    # if stat_list == 'stats':

    # if year_list == 'years':

    new_country_list = country_list.split('&')
    new_stat_list = stat_list.split('&')
    new_year_list = year_list.split('&')
    without_y_year_list = []

    #our database needs to have 'y's in front of the years.
    for i in range(len(new_year_list)):
        without_y_year_list.append(new_year_list[i])
        new_year_list[i] = 'y' + new_year_list[i]

    #capitalizes the first letter in each country name

    alpha_country_list = sorted(new_country_list)


    #accesses our database to get the weo code for each country
    weo_list = []

    for country_name in new_country_list:
        weo_list.append(get_country_weo_by_name(country_name)['weo'])

    response_list = []
    #Builds a query for every stat and adds the json response to the response list that gets dumped
    for stat in new_stat_list:
        #builds the query
        query = 'SELECT '
        first = True
        for year in new_year_list:
            if first:
                first = False
                query += year
            else:
                query += ', '+ year
        query += ' FROM '+stat+' WHERE '
        first = True
        for code in weo_list:
            if first:
                query += 'weo_country_code = \''+code+'\'' 
                first = False
            else:
                query += ' OR weo_country_code =\''+code+'\''

        #queries the database with the built query and constructs the json response
        rows = _fetch_all_rows_for_query(query)
        if len(rows) == len(new_country_list):
            counter = 0
            #return json.dumps(rows[0])
            for row in rows:
                JSON_to_dump = {}
                for i in range(len(new_year_list)):
                    JSON_to_dump = {'country':alpha_country_list[counter], 'year':without_y_year_list[i], 'stat':stat, 'value':row[i]}
                    response_list.append(JSON_to_dump)
                counter += 1
        else:
            return '''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
            Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
            Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
            Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
            Possible years: Any year between 1980 and 2020. For a full list, check out a calendar!
            '''

    return json.dumps(response_list)


if __name__ == '__main__':
    '''
    Normally we would use the command line to receive the host and port number, but for the sake of running our tests with ease we have decided to hard code
    them into our main instead (so run with the command "python3 api.py"). We understand the lack of flexibility associated with this, so if you would like,
    uncomment the lines below and type the host and port numbers into Terminal instead.
    '''

    host = 'thacker.mathcs.carleton.edu'
    port = 5119

    # if len(sys.argv) != 3:
    #     print('Usage: {0} host port'.format(sys.argv[0]), file=sys.stderr)
    #     exit()

    # host = sys.argv[1]
    # port = sys.argv[2]  

    app.run(host=host, port=port)

