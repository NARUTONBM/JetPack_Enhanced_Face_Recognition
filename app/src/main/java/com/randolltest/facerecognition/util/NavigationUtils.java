package com.randolltest.facerecognition.util;

import com.blankj.utilcode.util.LogUtils;

import java.util.Objects;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

/**
 * Navigation 常用操作封装的工具类
 *
 * @author randoll.
 * @Date 4/29/20.
 * @Time 19:44.
 */
public class NavigationUtils {

    /**
     * 导航去某个页面
     *
     * @param navController 导航控制器
     * @param currentDestId 导航目前页面的id
     * @param naviActionId  导航操作的id
     * @return 导航的结果
     */
    public static boolean navi2(NavController navController, int currentDestId, int naviActionId) {
        try {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == currentDestId) {
                navController.navigate(naviActionId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 带参数导航去某个页面
     *
     * @param navController 导航控制器
     * @param currentDestId 导航目前页面的id
     * @param navDirections 带参数的导航操作
     * @return 导航的结果
     */
    public static boolean naviContainArguments2(NavController navController, int currentDestId, NavDirections navDirections) {
        try {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == currentDestId) {
                navController.navigate(navDirections);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 退出当前返回栈
     *
     * @param navController 导航控制器
     * @param popDestId     回退目标的id
     * @param currentDestId 当前目标的id
     * @param inclusive     是否需要重新进入该返回栈
     * @param naviActionId  导航操作的id
     * @return 回退的结果
     */
    public static boolean pop2(NavController navController, int popDestId, int currentDestId, boolean inclusive, int naviActionId) {
        try {
            return navController.popBackStack(popDestId, inclusive);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == currentDestId) {
                navController.navigate(naviActionId);
                return true;
            } else {
                return false;
            }
        }
    }
}
