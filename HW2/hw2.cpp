#include <iostream>
#include <vector>
#include <future>
#include <chrono>
using namespace std;

typedef vector<int> Data;

/// Heaper base class with all required methods. 
class Heaper {
public:
    Heaper(const Data *data) : n(data->size()), data(data) {
        interior = new Data(n - 1, 0);
    }

    virtual ~Heaper() {
        delete interior;
    }
    
    int n;
    const Data *data;
    Data *interior;
    
    virtual int value(int i) {
        if (i < n - 1)
            return interior->at(i);
        else
            return data->at(i - (n - 1));
    }

    virtual int parent(int i) {
        return (i - 1) / 2;
    }
    
    virtual int right(int i) {
        return (2 * i) + 2;
    }

    virtual int left(int i) {
        return (2 * i) + 1;
    }

    virtual bool isLeaf(int i) {
        if (i < n - 1)
            return false;
        else
            return true;
    }
};

class SumHeap : public Heaper {
    Data* prefixSum;
public:
    SumHeap(const Data *data) : Heaper(data) {
       calcSum(0, 0);
    }
    /// calcSum recursive method to calculate pairwise sum
    /// level parameter is for keeping track of 4 levels for parallel computing.
    void calcSum(int i, int level) {

        if (isLeaf(i))
            return;

        if (level < 4) {
            /// Executing in seperate thread parallely to the main thread when level is less than equal to 4
            auto handle = async(&SumHeap::calcSum, this, right(i), level + 1);
            /// Executing in main thread
            calcSum(left(i), level + 1);
            handle.get();
        } 
        else {
            calcSum(right(i), level + 1);
            calcSum(left(i), level + 1);
        }   
        interior->at(i) = value(left(i)) + value(right(i));
    }

    /// calcPrefix recursive method to calculate prefix values
    void calcPrefix(int i, int parentVal, int level) {
        
        if (isLeaf(i)) {
            prefixSum->at(i + 1 - n) = parentVal + value(i);
            return;
        }

        if (level < 4) {
            int rightValue = parentVal + value(left(i));
            auto handle = async(&SumHeap::calcPrefix, this, right(i), rightValue , level + 1);
            calcPrefix(left(i), parentVal, level + 1);
            handle.get();
        }
        else {
            calcPrefix(left(i), parentVal, level + 1);
            calcPrefix(right(i), parentVal + value(left(i)), level+1);
        }
    }
    void prefixSums(Data* prefix) {
        prefixSum = prefix;
        calcPrefix(0, 0, 0); /// calling calcPrefix method with 3 parameters: index, parent value, and level
    }
};

const int N = 1<<26;

int main() {
    Data data(N, 1); // put a 1 in each element of the data array
    data[0] = 10;
    Data prefix(N, 1);

    // start timer
    auto start = chrono::steady_clock::now();

    SumHeap heap(&data);
    heap.prefixSums(&prefix);

    // stop timer
    auto end = chrono::steady_clock::now();
    auto elpased = chrono::duration<double,milli>(end-start).count();

    int check = 10;
    for (int elem: prefix)
        if (elem != check++) {
            cout << "FAILED RESULT at " << check-1;
            break;
        }
    cout << "in " << elpased << "ms" << endl;
    return 0;
}
