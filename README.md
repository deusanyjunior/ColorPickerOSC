# ColorPickerOSC

<br/>

Pick a color and send through OSC. 
The user selects the color and the alpha through a color picker interface.
Each modification is sent through OSC to an IP defined on the settings.
Actually, the UnicastUDP is the only protocol available.
The settings have options to define the IP and Port.

The OSC messages follow the structure:
- /colorpickerosc float float float float

The float order depends on the color mode:
- ARGB
- RGBA

TODO:
- add Multicast support to Arduino
- add preference option to change the OSC mapping
- send values regularly as an option
- add OLA osc format
- add channel mapping to app interface
- add sliders for colors
- add scene record
- add automation


#### License
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
