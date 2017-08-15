package social.roo;

import jetbrick.template.JetGlobalContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import social.roo.model.dto.NodeDto;
import social.roo.model.entity.Node;
import social.roo.model.entity.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Roo
 *
 * @author biezhi
 * @date 2017/8/5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Roo {

    private JetGlobalContext    context  = null;
    private Map<String, String> settings = new HashMap<>();

    public boolean existSetting(String key) {
        return settings.containsKey(key);
    }

    public Roo context(Class<?> type, String key, Object value) {
        context.set(type, key, value);
        return this;
    }

    public Roo globalContext(JetGlobalContext context) {
        this.context = context;
        return this;
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }

    public Roo refreshNodes() {
        List<Node> parent = new Node().where("pid", 0).and("state", 1).findAll();
        List<NodeDto> nodes = parent.stream()
                .map(p -> {
                    NodeDto nodeDto = new NodeDto();
                    nodeDto.setTitle(p.getTitle());
                    nodeDto.setSlug(p.getSlug());
                    List<Node> children = new Node().where("pid", p.getId()).and("state", 1).findAll();
                    nodeDto.setChildren(children);
                    return nodeDto;
                })
                .collect(Collectors.toList());
        context.set(List.class, "nodes", nodes);
        return this;
    }

    public Roo refreshSettings() {
        List<Setting> settings = new Setting().findAll();
        Map<String, String> map = settings.stream()
                .collect(Collectors.toMap(x -> x.getSkey(), y -> y.getSvalue()));
        this.settings = map;
        context.set(Map.class, "settings", map);
        return this;
    }

    public static Roo me() {
        return RooCacheEnum.SINGLE.instance;
    }

    public enum RooCacheEnum {
        SINGLE;
        Roo instance;

        RooCacheEnum() {
            instance = new Roo();
        }
    }

}