package top.headfirst.funding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.headfirst.funding.config.ShortMessageProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LombokTest {

    @Autowired
    private ShortMessageProperties shortMessageProperties;

    private Logger logger = LoggerFactory.getLogger(LombokTest.class);

    @Test
    public void testLombokMethod(){
        shortMessageProperties.setTemplate_id("999");
        String template_id = shortMessageProperties.getTemplate_id();
        logger.info("template_id = " + template_id);
    }
}
