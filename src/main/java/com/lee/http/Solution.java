package com.lee.http;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Author Lee
 * @Date 2021/1/2
 */
public class Solution {
    public static void main(String[] args) {
    }

    private static void solution(int[] nums, int k) {
        // int[] {num, index}
        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] pair1, int[] pair2) {
                return pair1[0] >= pair2[0] ? pair1[0] - pair2[0] : pair2[0] - pair1[0];
            }
        });
        int[] ans = new int[nums.length - k + 1];
        for (int i = 0; i < k; i++) {
            priorityQueue.offer(new int[]{nums[i], i});
        }
        for (int l = 0, r = k - 1; r < nums.length; l++, r++) {

        }
    }
}
