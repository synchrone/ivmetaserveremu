ivmetaserveremu
===============

An Introversion metaserver tracks defcon multplayer games. The aim of this project is to reverse-engineer and produce opensource replacement for it, so that if introversion server somehow closes down - players could still play

What is done
============
Replys to AuthKey? (key is always non-banned)

UpdateURL

DemoLimits? (6 demo players max, 6 players is max game-size hardcoded)

MatchMaking? (fully-working serverbrowser responses and games-tracking)

Client-modification to rely on metaserver key-checking, not the client's

Web frontend to release keys and add them to whitelist. Keys are bound to email to prevent excessive cheating.

What to do
==========
Code-cleanup and beautification

To run
======
use: java -jar defconauthemu.jar