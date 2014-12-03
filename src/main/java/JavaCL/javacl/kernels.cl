__kernel void box_muller(__global float* u1, __global float* u2, __global float* out, int n)
{
	int i = get_global_id(0);
	if (i >= n) return;
	
	float lu1 = log(u1[i]);
	float two = (float)2.0;
	float tmp1 = sqrt(-two*lu1);
	float tmp2 = two*M_PI*u2[i];
	int i2 = 2*i;
	out[i2]	= tmp1*cos(tmp2);
	out[i2+1] = tmp1*sin(tmp2);
}