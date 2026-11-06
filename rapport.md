# Rapport pour le Projet PacMan

## Membres du groupe

INIZAN Klervi
DUBOIS-DELALOGE Eloïse
KASSEM Stefan

## Introduction

Dans ce projet, l'objectif était d'utiliser Java pour recréer le jeu PacMan.

## Réalisation

### Avancement du projet

Nous sommes actuellement dans l'étape 3.
Nous avons implémenté le Board, la Vue, le Contrôleur, PacMan et les fantômes.
Il y a dans notre jeu le compteur, la pause, les vies, les différents points et états des fantômes.
Pour l'instant, il est possible de manger un fantôme en ayant mangé un gros point, mais le fantôme ne retourne pas directement à l'enclos.
Les différents niveaux ne sont pas encore disponibles.

### Structure et hiérarchie de classe

Pour la Vue, on a créé la classe LesYeux qui implémente PacManView et qui gère toutes les images.
Pour le Contrôleur, on a implanté 2 classes qui implémentent de Controller : ControllerClass, qui contrôle le board test, et ControllerView, qui contrôle le board de jeu.
Pour les acteurs, nous avons AbstractActor qui implémente Actor, PacMan et AbstractGhost qui étendent AbstractActor, pour le fonctionnement de PacMan et les fonctions communes des fantômes respectivement, et enfin les classes Blinky, Pinky, Inky et Clyde qui étendent AbstractGhost et gèrent les spécificités de leurs fantômes respectifs.
Pour le Maze, nous avons uniquement MazeClass qui implémente Maze et gère les tuiles du board.
Enfin, pour le Board, on a créé la classe AbstractBoard, qui implémente Board puis ClassicBoard et TestBoard qui héritent AbstractBoard qui gèrent les boards de jeu et de test respectivement, et ColorBoard qui sert à dessiner les objets du board.

### Tests

Tous les tests de PacMan, SimpleController, Maze et les fantômes fonctionnent.
Dans le board, les tests de niveaux ne fonctionnent pas, puisqu'ils ne sont pas implémentés, les tests de Frighten en fonction du niveau, les tests de DotCounter en fonction de newLife/newLevel, les tests de specialDotCounter, le test de fantôme coincé et le test de la vitesse d'un fantôme mort.

## Organisation du travail

Au départ, nous avons décidé de répartir le travail en 3 groupes : le contrôleur et la vue pour Klervi, les acteurs pour Stefan et le maze et le board pour Eloïse. Vers la fin du projet, il y avait plus de mélange, chacun modifiait le code des autres parties pour faire fonctionner ce qu'il implémentait dans la sienne, à l'exception de la vue qui restait propre à Klervi.

## Prise de recul

### Quels progrès ?

Sans aucun doute, nous avons tous largement progressé en Java, mais aussi dans la logique nécessaire à la création d'un jeu, notamment dans la compréhension de l'ordre dans lequel les choses doivent être faites (établir la logique souhaitée, réfléchir à sa faisabilité, implémenter par étape, vérifier le fonctionnement, ...).

### Quelles difficultés ?

Dans le projet, Stefan a mis beaucoup de temps à implémenter les logiques de déplacement des fantômes, étant tous différents, Klervi a eu des difficultés à comprendre comment faire le lien entre les classes et le contrôleur et Eloïse a dû s'arracher les cheveux sur nextFrame.
Pour remédier à ces obstacles, nous avons persévéré et parfois réfléchi à plusieurs pour trouver d'autres points de vue.

## Conclusion

Globalement,  nous sommes très satisfaits de notre progression, nous ne pensions pas arriver jusqu'à l'étape 3, encore moins aussi loin dans l'étape. Si nous avions plus de temps, notre objectif serait d'implémenter les niveaux.
Le projet nous a apporté un jeu, du fun et une bonne quantité de frustration envers les tests.
