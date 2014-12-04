MonteCarloCL
============

Monte Carlo generator using GPU's to generate Gaussian Random Variables

See the JUnit test TestMonteCarlo for an example of how to implement the Classes to
price options.

The GPUGaussianGenerator fits exactly into the framework of the original MonteCarlo
project with the exception of doubles being replaced by floats due to GPU limitations.
