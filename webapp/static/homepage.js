function isIn(value, array) {
  return array.indexOf(value) > -1;
}

function onStatButton(element) {
    // toggle button colors on click
	if (element.style.backgroundColor == 'white'){
		element.style.backgroundColor = 'lightgreen';
	}
    else {
    	element.style.backgroundColor = 'white';
    }
}

function onAllStatsButton(element){
    // toggles all button colors
    var buttonList = ['gdp_stat', 'gdp_per_cap_stat', 'inflation_stat', 'debt_stat', 'revenue_stat', 'unemployment_stat', 'population_stat'];
    if (element.style.backgroundColor =='white'){
        element.style.backgroundColor = 'lightgreen';
        for (var stat in buttonList){
            document.getElementById(buttonList[stat]).style.backgroundColor = 'lightgreen';
        }
    }
    else{
        element.style.backgroundColor = 'white';
        for (var stat in buttonList){
            document.getElementById(buttonList[stat]).style.backgroundColor = 'white';
        }
    }
}

function onAllYearsButton(element){
    // fills year field with full range
    document.getElementById(element).value = '1980-2010';
}

function searchCallback(responseText){
    var responseList = JSON.parse(responseText);
    var countryList = [];
    var yearList = [];
    var statList = [];
    // iterate through response to lists of all countries, years and stats
    for (var j = 0; j < responseList.length; j++){

        if (!(isIn(responseList[j]['country'], countryList))) {
            countryList.push(responseList[j]['country']);
        }
        if (!(isIn(responseList[j]['year'], yearList))) {
            yearList.push(responseList[j]['year']);
        }
        if (!(isIn(responseList[j]['stat'], statList))) {
            statList.push(responseList[j]['stat']);
        }
    }

    var responseCounter = 0;
    //var tableList = [];
    var tableBody = '';
    for (var stat in statList) {

        if (statList[stat] == 'Unemployment_rate') {
            statList[stat] = 'Unemployment rate (percent of total labor force)';
        }
        else if (statList[stat] == 'GDP_per_capita') {
            statList[stat] = 'GDP per capita (national currency units per person)';
        }
        else if (statList[stat] == 'GDP') {
            statList[stat] = 'GDP (billions of national currency)';
        }
        else if (statList[stat] == 'Population') {
            statList[stat] = 'Population (millions)';
        }
        else if (statList[stat] == 'Inflation') {
            statList[stat] = 'Inflation (index)';
        }
        else if (statList[stat] == 'Debt') {
            statList[stat] = 'Debt (billions of national currency)';
        }
        else if (statList[stat] == 'Revenue') {
            statList[stat] = 'Revenue (billions of national currency)';
        }

        tableBody += '<tr><th>'+statList[stat]+'</th></tr>';

        // table header consists of the countries header and relevant years
        tableBody += '<tr><th>Country</th>';
        for (var y = 0; y < yearList.length; y++) {
            tableBody += '<th>' + yearList[y] + '</th>';
        }
        tableBody += '</tr>';

        // one row for each item in countryList
        // responseCounter keeps track of place in responseList
        
        for (var k = 0; k < countryList.length; k++) {

            tableBody += '<tr><td>' + countryList[k] + '</td>';

            for (var z = 0; z < yearList.length; z++) {
                tableBody += '<td>' + responseList[responseCounter]['value'] + '</td>';
                responseCounter++;
            }
            tableBody += '</tr>';
        }
    }
    document.getElementById('results_table').innerHTML = tableBody;
    document.getElementById('error_msg').innerHTML = '';
}

function errorMessageResponse() {
    document.getElementById('results_table').innerHTML = '';
    document.getElementById('error_msg').innerHTML = 'Invalid input! You need to enter one or more of the valid countries, years and statistics. Multiple countries and years must be separated by a comma and a space. Year ranges may be denoted with a hyphen. For more information, see the links above.';
}

function onSearchButton() {
    // separate by commas to construct countries list
    var countriesList = document.getElementById('countries').value.split(', ');

    // separate by commas and hyphens to construct year list
    var years = document.getElementById('years').value;
    var yearsList = years.split(', ');
    for (year in yearsList) {
        if (isIn('-', yearsList[year])) {
            var yearRange = yearsList[year].split('-');
            yearsList[year] = yearRange[1];
            for (var r = parseInt(yearRange[0]); r < parseInt(yearRange[1]); r++) {
                yearsList.push(r);
            }
        }
    }
    yearsList.sort();

    var statList = ['GDP', 'GDP_per_capita', 'Inflation', 'Debt', 'Revenue', 'Unemployment_rate', 'Population'];
    var buttonList = ['gdp_stat', 'gdp_per_cap_stat', 'inflation_stat', 'debt_stat', 'revenue_stat', 'unemployment_stat', 'population_stat'];
    var gdpButton = document.getElementById('gdp_stat');
    var inflationButton = document.getElementById('inflation_stat');
    var url = '/';
    var first = true;
    var statButton;

    // construct the URL
    for (var stat in statList){
        statButton = document.getElementById(buttonList[stat]);
        // add stat if selected
        if (!(statButton.style.backgroundColor == 'white')){
            if(first){
                url += statList[stat];
                first = false;
            }
            else{
                url += '&' + statList[stat];
            }
        }
        
    }

    url+='/';

    first = true;
    for (var country in countriesList){
        if(first){
            url += countriesList[country];
            first = false;
        }
        else{
            url += '&' + countriesList[country];
        }
    }

    url+='/';

    first = true;
    for (var year in yearsList){
        if(first){
            url += yearsList[year];
            first = false;
        }
        else{
            url += '&' + yearsList[year];
        }
    } 

    xmlHttpRequest = new XMLHttpRequest();
    xmlHttpRequest.open('get', url);

    // return error message if http response anything other than 200
    xmlHttpRequest.onreadystatechange = function() {
            if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) { 
                searchCallback(xmlHttpRequest.responseText);
            }
            else {
                errorMessageResponse();
            }
        };

    xmlHttpRequest.send(null);
}
