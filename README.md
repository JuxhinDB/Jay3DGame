Jay3DGame
=========

><p>This is mainly a side-project(to test the current strength of my engine) that is based off my engine's latest
>commit(Commit #57) if you wish to review it at the point in time. </p>

><p>The engine itself won't be touched in any way(or maybe some minor reworks here and there), therefore anything I need to add
>will be done once the side-project is done.</p>

Things to add to the engine:
---------
> 1. Insert game inside the engine
2. Better scratchpad support
3. Better textures (reading, blitting, basic image processing)
4. Some way to seperate Game and Engine data
5. Add higher level rendering constructs (Shaders, materials, meshes, transforms)
6. Add some form of automatic shader selection
7. Add some form of primitives(Rectangle, circles, spheres, triangles)
8. More flexible transform class(Default values for projection and camera)
9. Non-perspective views (Orthogonal views)
10. Texture manipulation (Translation, rotation, scale)
11. More control over textures(filtering, formatting etc.)
12. More friendly constructors(Camera)
13. Window should have more properties (Center position, fullscreen, maybe mouse locking)
14. Better Vector math(normalisation, +=, commonly used vector constants, swizzling support)
15. Options class/system, some place to read values that the player chooses
16. Centralised "level" or "scene" class that holds the data that the game is using
17. Game object class
18. Make naming consistent
19. Eventually make easier way to generate Mesh
20. Eventually make easier way of generating texture coordinates
21. Vector swizzling
22. Make time seconds instead of nanoseconds
23. Give vectors a copy constructor
24. Give vectors an interpolation method
25. Transparency support
26. Sprite support
27. Good way to compare vectors
28. Vector2f crossproduct method29. 
29. Some way to actually display text(UI)


Notes:
-------
Was relatively fun yet very tedious to code the game. I basically said a big ?#@! you to OO design and have not refactored much code(to stick to the idea of not altering my engine). However it DEFINITELY helped pinpoint the weaknesses of my engine(as of commit #57 in Jay3D public repository)
