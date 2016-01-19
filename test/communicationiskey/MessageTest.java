package communicationiskey;

import battlecode.common.MapLocation;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author james
 */
public class MessageTest {
    @Test
    public void testGetFlag() {
        assertEquals(true, Message.getFlag(0b1L, 0));
        assertEquals(true, Message.getFlag(0b10L, 1));

        assertEquals(false, Message.getFlag(0b0L, 0));
        assertEquals(false, Message.getFlag(0b1000010L, 3));
    }

    @Test
    public void testSetFlag() {
        assertEquals(0b1001L, Message.setFlag(0b1000L, 0, true));
        assertEquals(0b1000L, Message.setFlag(0b1000L, 0, false));
        assertEquals(0b101011L, Message.setFlag(0b100011L, 3, true));
        assertEquals(0b11L, Message.setFlag(0b100011L, 5, false));
    }

    @Test
    public void testGetUnsigned() {
        assertEquals(0b1L, Message.getUnsigned(0b1L, 0, 1));
        assertEquals(0b1010L, Message.getUnsigned(0b1010L, 0, 4));

        assertEquals(0b1010L, Message.getUnsigned(0b10101L, 1, 4));
    }

    @Test
    public void testSetUnsigned() {
        assertEquals(0b1001000L, Message.setUnsigned(0b0L, 3, 4, 0b1001L));

        assertEquals(0b11010010L, Message.setUnsigned(0b10110110L, 2, 5, 0b10100L));
    }

    @Test
    public void testArrayRoundTrip() {
        assertEquals(0xf239bce1230a100dL, Message.fromIntArray(Message.fromLong(0xf239bce1230a100dL)));

        int[] ints = {0xf2239420,0xe29df202};

        assertArrayEquals(ints, Message.fromLong(Message.fromIntArray(ints)));
    }

    @Test
    public void testMessageRoundTrip() {
        Message message = new Message(Message.Command.ATTACK, new MapLocation(400, 400));

        int[] serialized = message.toSignalPayload();

        Message back = new Message(serialized);

        assertEquals(message, back);
    }
}
