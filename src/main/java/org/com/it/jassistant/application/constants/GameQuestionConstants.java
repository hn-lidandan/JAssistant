package org.com.it.jassistant.application.constants;

import java.util.List;

public final class GameQuestionConstants {

    private GameQuestionConstants() {
    }
    //最终这局初始化时，程序会在 5~15 之间随机一个分数作为开局分。
    public static final List<QuestionSeed> QUESTION_SEEDS = List.of(
            new QuestionSeed("昨天我和你认真说话的时候，你一直低头玩手机，根本没在听。", 18, 28),
            new QuestionSeed("我都说了今天心情不好，你还一直拿我开玩笑。", 15, 25),
            new QuestionSeed("我给你发了那么长一段消息，你最后只回了一个“哦”。", 20, 30),
            new QuestionSeed("你答应陪我吃饭，结果临时放我鸽子，还没提前和我说。", 10, 20),
            new QuestionSeed("我问你我和闺蜜谁更好看，你居然说各有各的美。", 5, 15),
            new QuestionSeed("上次吵架后我那么难过，你到现在都没有认真哄过我。", 8, 18),
            new QuestionSeed("纪念日你居然忘了，还要我提醒你。", 5, 15),
            new QuestionSeed("我精心打扮问你好不好看，你居然只说了一句“还行”。", 12, 22)
    );

    public record QuestionSeed(String reason, int minInitScore, int maxInitScore) {
    }
}
