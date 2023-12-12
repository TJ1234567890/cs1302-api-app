# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> Please provide a friendly description of your app, including
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

My App takes the input of a airport name and output the air quality and weather of the area.
The user is allowed to interact with the intput field and load button. When the user inputs some name similar
the user is returned a list of airports with locations they are associated with so that they can find their airport easier.
The user re-enters the name of that airport and they will get the weather and pollution of that air quality in the area.
My first API, the airport API, takes in the users search and returns the airports city, latitude, longitude, and region. My second API, IQAir API, takes in the lat and lon to find a nearby city and give the air quality of and pollutants
    Repository Link: https://github.com/TJ1234567890/cs1302-api-app/tree/main

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### API 1

```
https://api.api-ninjas.com/v1/airports?name=A%20
```

> API 1 uses a header for its API key

### API 2

```
http://api.airvisual.com/v2/nearest_city?lat=51.4706001282&lon=-0.4619410038&key=4e1e0d5d-e9ba-45af-9c11-f61a94c13c49
```

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

I learned that I can use different tools to check if API's work properly.
This helps me check if API's work without having to build work request for one api,
then change it when it dosent work. The tool Postman made it easier to test if a api worked as intended
before I implemented into my code.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

I would Change the air quality API I used because I found another one right as I was done that had images.
I probably would have also used it in conjunction with another API. Not the Airport API. Making it easier to check
Air quality in travel destinations.
