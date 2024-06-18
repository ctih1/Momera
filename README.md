# Momera
A program that extracts the motion from your camera.
![image](https://github.com/ctih1/Momera/assets/78687256/cf7080e4-4673-4d5c-a244-abb42b0db809)

## Introduction
This is a program that I made using python, because I was interested on motion extraction (after watching [Posy's excellent video](<https://www.youtube.com/watch?v=NSS6yAMZF78>))

## Installation
This program is very easy to setup.
* Using Git:

    <ol>
    <li>

    ```git clone https://github.com/ctih1/Momera```

    </li>
    <li>

    ```cd Momera```
    
    </li>
    <li>

    ```pip install -r requirements.txt```

    </li>
    <li>

    ```cd src```

    </li>
    <li>

    ```python3 main.py```

    </li>
    </ol>

## Usage
If you do not want to alter any settings, you can skip this section. If have issues, like the program using the wrong camera, you should follow this guide

| Key | Action                                          
|-----|-------------------------------------------------
| Q   | Quit the application                            
| H   | Disable motion extractor to see the input video 
| K   | Change camera (left)                            
| L   | Change the camera (right)                       
| Mousewheel Up| Alter the frame delay by +1            
| Mousewheel Down | Alter the frame delay by -1         

### Notifications
You may see some notifications when doing actions, or when getting errors. They are pretty self-explanatory, but on some errors, it might be hard to find a cause. Here's the most common one:
```Failed camera switch #x```
There are two reasons why this might happen: 
* This camera doesn't exist.
* The camera's width or height is listed as 0

## Contribution
<ol>
    <li>Clone the repo</li>
    <li>Make a new branch in the following format: ("fixes" or "features")/(what you did)</li>
    <li>Push your branch into GitHub</li>
    <li>Make a pull request to master</li>
</ol>