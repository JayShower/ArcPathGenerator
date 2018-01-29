# BezierPathGenerator



Feel free to add issues, make pull requests, etc.

This project uses...

-code from team 254 (mostly the trapezoidal motion stuff): https://github.com/Team254/FRC-2017-Public

-ideas from this primer on BezierCurves: https://pomax.github.io/bezierinfo/#arclength

-guass quadrature integral approximation: https://pomax.github.io/bezierinfo/legendre-gauss.html#n64

-the Falcon Path Planner graphing utility to graph paths, because I came across that when researching and it is easy to use. Falcon Path Planner: https://github.com/KHEngineering/SmoothPathPlanner

This project also has Jaci's PathFinder in it because I wanted to run some speed comparison tests

Other notes:

-Another way to get smooth turning would be with a track transition curve (curvature depends on arc length): https://en.wikipedia.org/wiki/Track_transition_curve

-I would like to eventually include ideas from this paper: https://www.researchgate.net/publication/221592023_G3_Transition_Curve_Between_Two_Straight_Lines

^ it would be neat to do that. If someone wants to work with me on that, that would be great. I have a general idea of what's going on in that paper (connect two monotonically increasing in curvature bezier curves with an arc) but have no idea how I would go about implementing that.
