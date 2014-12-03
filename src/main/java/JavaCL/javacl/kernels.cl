__kernel void box_muller(__global double* u1, __global double* u2, __global double* out, int n)
{
	int i = get_global_id(0);
	if (i >= n) return;
	
	double lu1 = log(u1[i]);
	double tmp1 = sqrt(-2.0*lu1);
	double tmp2 = 2.0*M_PI*u2[i];
	int i2 = 2*i;
	out[i2]	= tmp1*cos(tmp2);
	out[i2+1] = tmp1*sin(tmp2);
}