package communicationiskey;

import battlecode.common.MapLocation;

/**
 * @author james
 */
public class Message {

    static private final int COMMAND_START = 0;
    static private final int COMMAND_LENGTH = 1;

    static private final int TARGET_X_START = COMMAND_START + COMMAND_LENGTH;
    static private final int TARGET_X_LENGTH = 10;

    static private final int TARGET_Y_START = TARGET_X_START + TARGET_X_LENGTH;
    static private final int TARGET_Y_LENGTH = 10;

    public enum Command {
        ATTACK,
        RUN_AWAY
    }

    /**
     * The command this message contains.
     */
    public final Command command;

    /**
     * The location the command targets.
     */
    public final MapLocation target;

    public Message(Command command, MapLocation target) {
        this.command = command;
        this.target = target;
    }

    public Message(int[] signalPayload) {
        long array = fromIntArray(signalPayload);

        this.command = Command.values()[(int) getUnsigned(array, COMMAND_START, COMMAND_LENGTH)];

        this.target = new MapLocation(
                (int) getUnsigned(array, TARGET_X_START, TARGET_X_LENGTH),
                (int) getUnsigned(array, TARGET_Y_START, TARGET_Y_LENGTH)
        );
    }

    /**
     * @return an int[] suitable to be broadcast
     */
    public int[] toSignalPayload() {
        long array = 0;

        array = setUnsigned(array, COMMAND_START, COMMAND_LENGTH, this.command.ordinal());
        array = setUnsigned(array, TARGET_X_START, TARGET_X_LENGTH, this.target.x);
        array = setUnsigned(array, TARGET_Y_START, TARGET_Y_LENGTH, this.target.y);

        return fromLong(array);
    }

    /**
     * @param array the long to extract the flag from
     * @param index the index of the flag in the array
     * @return the value of the flag
     */
    public static boolean getFlag(long array, int index) {
        // shift the array so the bit is in the 0 index
        long shiftedArray = array >>> index;

        // mask the array so that the only possible values
        // are 0 and 1
        long masked = shiftedArray & 0b1;

        // if the value is 1, the flag is true
        return masked == 1;
    }

    /**
     * @param array the array to modify
     * @param index the index of the flag to set
     * @param value the value of the flag
     * @return a modified array with the flag set
     */
    public static long setFlag(long array, int index, boolean value) {
        if (value) {
            return array | (1 << index);
        } else {
            return array & ~(1 << index);
        }
    }

    /**
     * @param array the array to read
     * @param startIndex the start index of the integer to read
     * @param length the length of the integer to read
     * @return an integer extracted from the array
     */
    public static long getUnsigned(long array, int startIndex, int length) {
        // get rid of lower bits in the array
        long removedLowedBits = array >>> startIndex;

        // get rid of upper bits in the array
        long removedUpperBits = removedLowedBits << 64 - length;

        // shift the array into the correct place
        return removedUpperBits >>> 64 - length;
    }

    /**
     * @param array the array to modify
     * @param startIndex the start index of the integer to write
     * @param length the length of the integer to write
     * @param value the value to set the output array to
     * @return a modified version of the array
     */
    public static long setUnsigned(long array, int startIndex, int length, long value) {
        // this is a slow implementation. Can you make it use less bytecode?

        // read the current value in the chunk of the array
        long currentValue = getUnsigned(array, startIndex, length);

        // zero the chunk of the array
        long zeroedArray = array ^ (currentValue << startIndex);

        // write the value to the array
        return zeroedArray | (value << startIndex);
    }

    /**
     * @param array the array to convert
     * @return a long representation of the array
     */
    public static long fromIntArray(int[] array) {
        return ((long)array[0] << 32) | array[1] & 0xFFFFFFFFL;
    }

    /**
     * @param array the long to convert
     * @return an int[] representation of the array
     */
    public static int[] fromLong(long array) {
        return new int[] {
                (int)(array >> 32),
                (int)array
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (command != message.command) return false;
        return target != null ? target.equals(message.target) : message.target == null;

    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }
}
