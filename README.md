# Emotion Detector

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://github.com/AntonioMarceddu/Emotion_Detector)

[![Generic badge](https://img.shields.io/badge/Uses-OpenCV-blueviolet.svg)](https://github.com/AntonioMarceddu/Emotion_Detector)
[![Generic badge](https://img.shields.io/badge/Uses-DL4J-yellow.svg)](https://github.com/AntonioMarceddu/Emotion_Detector)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/AntonioMarceddu/Emotion_Detector/blob/master/LICENSE.txt)
[![Generic badge](https://img.shields.io/badge/Version-1.0.0-71bdef.svg)](https://github.com/AntonioMarceddu/Emotion_Detector)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/AntonioMarceddu/Emotion_Detector/graphs/commit-activity)
[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)](https://www.linkedin.com/in/antonio-marceddu/)

<p align="center">
    <img src="https://github.com/AntonioMarceddu/Emotion_Detector/blob/master/resources/ED.png"><br>
	Screenshot of Emotion Detector
</p>

Description available in english and italian below. License is at the bottom of page.

## English

### Emotion Detector
Emotion Detector is a program that offers the possibility to perform facial expressions recognition directly from a camera, from an image or a video file. It was developed by me, in parallel with [FEDC](https://github.com/AntonioMarceddu/Facial_Expressions_Databases_Classifier), using Java as the programming language, together with the [OpenCV](https://opencv.org/) and [DeepLearning4J](https://deeplearning4j.org/) libraries, the [JavaFX Maven Plugin](https://github.com/javafx-maven-plugin/javafx-maven-plugin) and the [Apache Maven](http://maven.apache.org/) tool.

### Additional infomation about the program
As happened for FEDC, its development began in 2018 as part of a project for the Computer Vision course of the Politecnico di Torino. Initially, it was intended as a support tool for facial expressions recognition, which could be used to capture the user's face using a camera and test a neural network trained for that purpose. During my master's thesis, it was improved and corrected by adding a box containing the image that will actually be sent to the neural network, a function to acquire a screenshot of the program immediately after a prediction so as to document the results obtained and much more. Its improvement continued during a research work on the calibration of autonomous driving vehicles, in which the possibility of searching for facial expressions in the frames of a video file and more was added.

During these two years, Emotion Detector has improved a lot and several functions have been added: so, I decided to follow the same path that I followed with FEDC and I released its complete code, under MIT license, on GitHub.

You can find an already compiled version for Windows [here](http://antoniomarceddu.altervista.org/en/ed.htm).

### Installation guide for Windows 10
Due to the different libraries present, the execution of Emotion Detector is not simple; for this reason, I have written a small guide to be able to do it correctly:

1. Download and install the [Java SE Development Kit 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html);
2. Download and install the [Eclipse IDE for Java Developers](https://www.eclipse.org/);
3. Set the environment variable *JAVA_HOME* to point to the JDK folder. If you do not know how to do this operation, you can resort to this [guide](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html);
4. Open Eclipse and install the [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html) extension. You can do it from the menu by clicking on the *Help* -> *Install New Software…* option, clicking the *Add* button and inserting *e(fx)clipse* in the *Name* text box and *https://download.eclipse.org/efxclipse/updates-nightly/site/* in the *Location* text box. Once inserted, click the *Add* button, select *E(fx)clipse IDE* from the list and install it.
6. From the menu, click the *Run* -> *Run Configurations* option and create a new *Maven Build* one. Set the *Base directory* as *{$project_loc:emotion-detector}* and *jfx:native* as the *Goals* and click the *Run* button. 
7. After this step, you can also create and use a new *Java Application* configuration from the same window, so as to execute the program more easily.

### Updates
* 07/05/2020 - Version 1.0.0 released.

## Italian

### Emotion Detector
Emotion Detector è un programma che offre la possibilità di eseguire il riconoscimento delle espressioni facciali direttamente da una fotocamera, da un'immagine o da un file video. È stato sviluppato da me, parallelamente a [FEDC](https://github.com/AntonioMarceddu/Facial_Expressions_Database_Classifier), utilizzando Java come linguaggio di programmazione, insieme alle librerie [OpenCV](https://opencv.org/) e [DeepLearning4J](https://deeplearning4j.org/), al [JavaFX Maven Plugin](https://github.com/javafx-maven-plugin/javafx-maven-plugin) e il tool [Apache Maven](http://maven.apache.org/).

### Informazioni addizionali riguardo al programma
Come avvenuto per FEDC, il suo sviluppo è iniziato nel 2018 come parte di un progetto per il corso di Computer Vision del Politecnico di Torino. Inizialmente, era inteso come strumento di supporto per il riconoscimento delle espressioni facciali, che poteva essere utilizzato per catturare il viso dell'utente utilizzando una fotocamera e testare una rete neurale addestrata per tale scopo. Durante la mia tesi di laurea magistrale, è stato migliorato e corretto aggiungendo un box contenente l'immagine che verrà effettivamente inviata alla rete neurale, una funzione per acquisire uno screenshot del programma immediatamente dopo una predizione così da documentare i risultati ottenuti e molto altro ancora. Il suo miglioramento è continuato durante un lavoro di ricerca riguardo la calibrazione di veicoli a guida autonoma, in cui è stata aggiunta la possibilità di cercare espressioni facciali nei frame di un file video e altro ancora.

Durante questi due anni, Emotion Detector è migliorato molto e sono state aggiunte diverse funzioni: ho deciso così di seguire lo stesso percorso che ho seguito con FEDC e ho rilasciato il suo codice completo, sotto licenza MIT, su GitHub.

Puoi trovare una versione già compilata per Windows [qui](http://antoniomarceddu.altervista.org/en/ed.htm).

### Guida all'installazione per Windows 10
A causa delle diverse librerie presenti, l'esecuzione di Emotion Detector non è semplice; per questo motivo, ho scritto una piccola guida per poterlo effettuare nella maniera corretta:

1. Scarica e installa lo [Java SE Development Kit 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html);
2. Scarica e installa l'[Eclipse IDE for Java Developers](https://www.eclipse.org/);
3. Impostare la variabile di ambiente *JAVA_HOME* in modo che punti al JDK. Se non sai come effettuare questa operazione, puoi fare ricorso a questa [guida](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html);
4. Apri Eclipse ed installa l'estensione [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html). Puoi farlo dal menù facendo clic su sull'opzione *Help* -> *Install New Software…*, facendo clic sul pulsante *Add* e inserendo *e(fx)clipse* nella casella di testo *Name* e *https://download.eclipse.org/efxclipse/updates-nightly/site/* nella casella di testo *Location*. Una volta inseriti, fai clic sul pulsante *Add*, seleziona *E(fx)clipse IDE* dalla lista e installalo.
6. Dal menù, fai clic sull'opzione *Run* -> *Run Configurations* e crea una nuova *Maven Build*. Imposta la *Base directory* come *{$project_loc:emotion-detector}* e *jfx:native* come *Goals* e clicca il pulsante *Run*. 
7. Dopo questo passaggio, è anche possibile creare e utilizzare una nuova configurazione *Java Application* dalla stessa finestra, così da eseguire il programma più facilmente.

### Aggiornamenti
* 07/05/2020 - Versione 1.0.0 rilasciata.

## License
MIT License

Copyright (c) 2020 Antonio Costantino Marceddu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
