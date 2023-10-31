import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MergeSortComparison {
    
    public static class MergeSortTask extends RecursiveTask<int[]> {
        private int[] arr;

        public MergeSortTask(int[] arr) {
            this.arr = arr;
        }

        @Override
        protected int[] compute() {
            if (arr.length <= 1) {
                return arr;
            }

            int middle = arr.length / 2;
            int[] left = new MergeSortTask(Arrays.copyOfRange(arr, 0, middle)).fork().join();
            int[] right = new MergeSortTask(Arrays.copyOfRange(arr, middle, arr.length)).fork().join();

            return merge(left, right);
        }

        private int[] merge(int[] left, int[] right) {
            int[] result = new int[left.length + right.length];
            int i = 0, j = 0, k = 0;
            while (i < left.length && j < right.length) {
                if (left[i] < right[j]) {
                    result[k++] = left[i++];
                } else {
                    result[k++] = right[j++];
                }
            }
            while (i < left.length) {
                result[k++] = left[i++];
            }
            while (j < right.length) {
                result[k++] = right[j++];
            }
            return result;
        }
    }

    public static int[] parallelMergeSort(int[] arr) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new MergeSortTask(arr));
    }

    public static void merge(int arr[], int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        int L[] = new int[n1];
        int R[] = new int[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[m + 1 + j];

        int i = 0, j = 0;

        int k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    public static void mergeSort(int arr[], int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};

        // Merge Sort
        int[] mergeSortArr = Arrays.copyOf(arr, arr.length);
        long mergeSortStartTime = System.nanoTime();
        mergeSort(mergeSortArr, 0, mergeSortArr.length - 1);
        long mergeSortEndTime = System.nanoTime();
        long mergeSortTime = mergeSortEndTime - mergeSortStartTime;

        // Multithreaded Merge Sort
        int[] multiThreadedMergeSortArr = Arrays.copyOf(arr, arr.length);
        long multiThreadedMergeSortStartTime = System.nanoTime();
        multiThreadedMergeSortArr = parallelMergeSort(multiThreadedMergeSortArr);
        long multiThreadedMergeSortEndTime = System.nanoTime();
        long multiThreadedMergeSortTime = multiThreadedMergeSortEndTime - multiThreadedMergeSortStartTime;

        System.out.println("Original Array: " + Arrays.toString(arr));
        System.out.println("Merge Sort Result: " + Arrays.toString(mergeSortArr));
        System.out.println("Multithreaded Merge Sort Result: " + Arrays.toString(multiThreadedMergeSortArr));

        System.out.println("Merge Sort Time (ns): " + mergeSortTime);
        System.out.println("Multithreaded Merge Sort Time (ns): " + multiThreadedMergeSortTime);
    }
}
