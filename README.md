# My Maps 

## *Emmanuel Gyabaah*

**My Maps** displays a list of maps, each of which show user-defined markers with a title, description, and location. The user can also create a new map, change map type and search for maps. 

Time spent: **7.5** hours spent in total

## Functionality 

The following **required** functionality is completed:

* [x] The list of map titles is displayed.
* [x] After tapping on a map title, the associated markers in the map are shown.
* [x] The user is able to create a new map.

The following **extensions** are implemented:

* [x] When a map marker is created, the pin is animated.
* [x] In the initial screen, show the number of places in each map along with the title.
* [x] Instead of the default marker, use a custom marker drawable.
* [x] Add a menu option in the map activity to change the map type (e.g. normal vs terrain)
* [x] In the creation flow, add a button where the map will move to the user’s current location.
* [x] Add the ability to search for maps which contain a string in the title.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://i.imgur.com/a/vMB2Cr9.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with Windows snipping tool and [EZGIF](https://ezgif.com/)..

## Notes
TODO: Replace file storage with database later

Describe any challenges encountered while building the app.
* UI wasn't updating list of map tiles being displayed after implementing Filterable interface hence I had to rewrite a new RecycleView Adapter logic to extend ListAdapter (https://stackoverflow.com/a/69527742/25119769)

## License

    Copyright [yyyy] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
