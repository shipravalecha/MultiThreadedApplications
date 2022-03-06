/**
 * @file hw1.cpp - use two threads to speed up hw1_setup
 * @author Kevin Lundeen
 * @see "Seattle University, CPSC5600, Winter 2021"
 *
 * Design: 
 * 1. two threads encode all the numbers in the array
 * 2. the encoded numbers are prefix-summed in place in the main thread
 * 3. two threads decode all the numbers in the array 
 */
#include <iostream>
#include "ThreadGroup.h"
using namespace std;

/**
 * Provided encode function
 * Just does something time-consuming (and arbitary)
 *
 * @param v  number to encode
 * @returns  the encoded number
 */
int encode(int v) {
	for (int i = 0; i < 500; i++)
		v = ((v * v) + v) % 10;
	return v;
}

/**
 * Provided decode function
 * Just does something time-consuming (and arbitary)
 *
 * @param v  encoded number to decode
 * @returns  the decoded number
 */
int decode(int v) {
	return encode(v);
}

/**
 * @class ShareData - Structure to hold a pointer to the array we're working on
 * and also the "ownership" breakdown of the array amont the threads.
 */
struct ShareData {
	static const int N_THREADS = 2;  // keeps getting faster on CS1 up to about 32
	int start[N_THREADS];            // index into data where ownership starts
	int end[N_THREADS];              // where ownership ends (+1)
	int *data;                       // address of the subject data array
};

/**
 * @class EncodeThread - thread operation for the encoding step
 */
class EncodeThread {
public:
	/**
	 * Thread routine functor
	 *
	 * @param id           thread id
	 * @param sharedDataa  pointer to our ShareData structure.
	 */
	void operator()(int id, void *sharedData) {
		auto *ourData = (ShareData*)sharedData;
		for (int i = ourData->start[id]; i < ourData->end[id]; i++)
			ourData->data[i] = encode(ourData->data[i]);
	}
};

/**
 * @class DecodeThread - thread operation for the decoding step
 */
class DecodeThread {
public:
	/**
	 * Thread routine functor
	 *
	 * @param id           thread id
	 * @param sharedDataa  pointer to our ShareData structure.
	 */
	void operator()(int id, void *sharedData) {
		auto *ourData = (ShareData*)sharedData;
		for (int i = ourData->start[id]; i < ourData->end[id]; i++)
			ourData->data[i] = decode(ourData->data[i]);
	}
};

/**
 * Scans the given array using encode, then decodes the results and
 * replaces the contents of the array with the scan values..
 *
 * @param data    subject array
 * @param length  number of elements in data
 */
void prefixSums(int *data, int length) {
	// Setup our data to share (threads will get the address of this structure)
	ShareData ourData;
	ourData.start[0] = 0;
	int pieceLength = length/ShareData::N_THREADS;
    cout << "piecelength: " << pieceLength << endl;
    cout << "length: " << length << endl;
    cout << "ShareData::N_THREADS: " << ShareData::N_THREADS << endl;
    
	for (int t = 1; t < ShareData::N_THREADS; t++)
		ourData.end[t-1] = ourData.start[t] = ourData.start[t-1] + pieceLength;
    cout << "ourData.end[t-1]: " << ourData.end[0] << endl;
    cout << "ourData.start[t]: " << ourData.start[1] << endl;
    cout << "ourData.start[t-1]: " << ourData.start[0] << endl;
	ourData.end[ShareData::N_THREADS-1] = length;
	ourData.data = data;

	// Fork the worker threads to do the encoding of each element in the array
	// (then stored back into the array)
	ThreadGroup<EncodeThread> encoders;
	for (int t = 0; t < ShareData::N_THREADS; t++)
		encoders.createThread(t, &ourData);
	encoders.waitForAll();

	// Do the scan in the main thread of the encoded values
	// (stored back into the array)
	int encodedSum = 0;
	for (int i = 0; i < length; i++) {
		encodedSum += data[i];
		data[i] = encodedSum;
	}

	// For the worker threads to do the decoding of each element in the array
	// (stored back into the array)
	ThreadGroup<DecodeThread> decoders;
	for (int t = 0; t < ShareData::N_THREADS; t++)
		decoders.createThread(t, &ourData);
	decoders.waitForAll();

}

/**
 * Test harness for prefixSum.
 *
 * @returns 0 (success)
 */
int main() {
	int length = 1000 * 1000;

	// make array
	int *data = new int[length];
	for (int i = 1; i < length; i++)
		data[i] = 1;
	data[0] = 6;

	// transform array into converted/deconverted prefix sum of original
	prefixSums(data, length);

	// printed out result is 6, 6, and 2 when data[0] is 6 to start and the rest 1
	cout << "[0]: " << data[0] << endl
			<< "[" << length/2 << "]: " << data[length/2] << endl 
			<< "[end]: " << data[length-1] << endl; 
	delete[] data;
	return 0;
}

