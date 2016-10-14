'''
   api_tests.py
   Adapted from Jeff Ondich, 9 May 2012
   Updated 30 April 2016
'''

import api
import unittest
import json

class APITester(unittest.TestCase):

    def testEmptyQuery(self):
        print("empty query: "+api.get_country_stat_year("","",""))
        self.assertEqual(api.get_country_stat_year("","",""),'''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
            Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
            Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
            Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
            Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
            ''')

    def testBadSyntaxQuery(self):
        self.assertEqual(api.get_country_stat_year("Mexico","inflation","1998"),'''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
            Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
            Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
            Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
            Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
            ''')

    def testLowercaseQuery(self):
        self.assertEqual(api.get_country_stat_year("inflation","mexico","1998"),'''Usage: /stat_list/country_list/year_list, where items in each list are separated by ampersands (&).
            Also remember to check your spelling, and capitalize the first letter of each country name!\n\r
            Possible stats: gdp, gdp_per_capita, inflation, unemployment_rate, population, debt, revenue. For descriptions of these statistics, visit /statistics/!\n\r
            Possible countries: United States, France, Venezuela, etc. For a full list, visit /countries/!\n\r
            Possible years: Any year between 1980 and 2010. For a full list, check out a calendar!
            ''')

    def testSingleStatSingleCountrySingleYear(self):
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998"))[0]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998"))[0]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998"))[0]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998"))[0]["value"],"48.47")

    def testSingleStatMultipleCountriesSingleYear(self):
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[0]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[0]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[0]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[0]["value"],"91.25")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[1]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[1]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[1]["country"],"Canada")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico&Canada","1998"))[1]["value"],"48.47")

    def testMultipleStatsSingleCountrySingleYear(self):
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[0]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[0]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[0]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[0]["value"],"48.47")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[1]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[1]["stat"],"unemployment_rate")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[1]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation&unemployment_rate","Mexico","1998"))[1]["value"],"3.16")

    def testSingleStatSingleCountryMultipleYears(self):
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[0]["year"],"1998")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[0]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[0]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[0]["value"],"48.47")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[1]["year"],"1999")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[1]["stat"],"inflation")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[1]["country"],"Mexico")
        self.assertEqual(json.loads(api.get_country_stat_year("inflation","Mexico","1998&1999"))[1]["value"],"56.5")

if __name__ == '__main__':
    unittest.main()