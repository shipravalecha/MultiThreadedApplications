
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
 
 /**
  * Pair class to implement a pair type in java 
  * Reference : https://www.techiedelight.com/implement-pair-class-java/
  */

class Pair<U, V> 
{
    public final U first;       /// the first field of a pair
    public final V second;      /// the second field of a pair
 
    /// Constructs a new pair with specified values
    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
 
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
 
        Pair<?, ?> pair = (Pair<?, ?>) o;
 
        if (!first.equals(pair.first)) {
            return false;
        }
        return second.equals(pair.second);
    }
 
    @Override
    public int hashCode()
    {
        return 31 * first.hashCode() + second.hashCode();
    }
 
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
