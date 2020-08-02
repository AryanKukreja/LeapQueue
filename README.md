# LeapQueue Mobile Application
[![js-standard-style](https://img.shields.io/badge/code%20style-standard-brightgreen.svg?style=flat)](https://github.com/feross/standard)
[![GPLv3 license](https://img.shields.io/badge/License-GPLv2-blue.svg)](http://perso.crans.org/besson/LICENSE.html)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)

This project is for a mobile application that lets users create (and view) reviews for stores that are specifically tailored to their performance during the COVID-19 Pandemic (and possible pandemics in the future 😬). The reviews will cover:

- Efficiency of staff personal (is there adequate staff to ensure social-distancing and other norms of the pandemic?)
- Item availability in the store 
- Queue times (for entering the stores, at checkout, etc...)
- Time the reviewer reached the store (this will help decide whether that is a good time to reach or not)

The name "LeapQueue" was chosen because users will be able to pick the best time to visit a particular store based on the reviews. 

# Motivation
Multiple sources of motivtion existed for this project:

1. This project was originally started as part of a Hackathon
2. My personal experience of waiting in the long queues in a store made me realize that people should be able to share information on their experiences at a store in a pandemic, so that others can prepare accordingly. If users are aware of long queues at a particular time, then they would pick another time to visit; this would help stagger out the arrival times more widely, and make the queues shorter at peak times (hopefully).

# Status
[![Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release suitable for the public.](https://www.repostatus.org/badges/latest/wip.svg)](https://www.repostatus.org/#wip)

This is a current work-in-progress, on the software develpment side as well as handling the logistics of going live.

# Tech Stack
At the basic level, this project is built with Gradle. Java is the programming language, and Android SDK is used atop it.
Other tools/APIs used were:

- Google Firebase: Used to maintain a list of users, and handle account sign-in/up (along with email verification). The FirestoreDB was used to save reviews, stores and non-auth information of users.
- Bing Maps: The **Local Search** API endpoint was used to find a store that the user searches. *As a result, this app currently only works in the USA*.
- Material UI