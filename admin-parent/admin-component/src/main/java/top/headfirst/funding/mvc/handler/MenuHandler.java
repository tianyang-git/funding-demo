package top.headfirst.funding.mvc.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.headfirst.funding.entity.Menu;
import top.headfirst.funding.service.api.MenuService;
import top.headfirst.funding.util.ResultEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MenuHandler {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/menu/get/whole/tree.json")
    public ResultEntity<Menu> getWholeTree(){
        List<Menu> menuList = menuService.getAll();
        Menu root = null;
        Map<Integer, Menu> menuMap = new HashMap<>();
        for (Menu menu:
             menuList) {
            Integer id = menu.getId();
            menuMap.put(id, menu);
        }
        for (Menu menu:
             menuList) {
            Integer pid = menu.getPid();
            // 根节点
            if (pid == null) {
                root = menu;
                continue;
            }
            // 非根节点，有父节点，获取父节点的 Menu, 将当前节点加入其子节点集合
            Menu father = menuMap.get(pid);
            father.getChildren().add(menu);
        }
        return ResultEntity.successWithData(root);
    }

    @RequestMapping("/menu/save.json")
    public ResultEntity<String> saveMenu(Menu menu){
        menuService.saveMenu(menu);
        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/menu/update.json")
    public ResultEntity<String> updateMenu(Menu menu){
        menuService.updateMenu(menu);
        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/menu/remove.json")
    public ResultEntity<String> removeMenu(@RequestParam("id") Integer id){
        menuService.removeMenu(id);
        return ResultEntity.successWithoutData();
    }

}
