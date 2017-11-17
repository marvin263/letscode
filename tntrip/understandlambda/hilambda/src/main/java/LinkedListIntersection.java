import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by nuc on 2016/1/3.
 */
public class LinkedListIntersection {
    public static class NodeAndPosition {
        ListNode node;
        int pos;

        NodeAndPosition(ListNode node, int pos) {
            this.node = node;
            this.pos = pos;
        }
    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        Deque<ListNode> stackA = new ArrayDeque<>();
        ListNode tmpA = headA;
        while (tmpA != null) {
            stackA.push(tmpA);
            tmpA = tmpA.next;
        }

        Deque<ListNode> stackB = new ArrayDeque<>();
        ListNode tmpB = headB;
        while (tmpB != null) {
            stackB.push(tmpB);
            tmpB = tmpB.next;
        }

        ListNode lastCommon = null;
        while (!stackA.isEmpty() && !stackB.isEmpty()) {
            tmpA = stackA.pop();
            tmpB = stackB.pop();
            if (tmpA == tmpB) {
                lastCommon = tmpA;
            } else {
                return lastCommon;
            }
        }
        return lastCommon;
    }


    public ListNode getIntersectionNode_1(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        NodeAndPosition lastCommon = null;
        NodeAndPosition nowA = getElement(headA, -1);
        NodeAndPosition nowB = getElement(headB, -1);
        while (nowA != null && nowB != null) {
            if (nowA.node == nowB.node) {
                lastCommon = nowA;
                if (nowA.pos == 0 || nowB.pos == 0) {
                    return lastCommon.node;
                } else {
                    nowA = getElement(headA, nowA.pos - 1);
                    nowB = getElement(headB, nowB.pos - 1);
                }
            } else {
                return lastCommon == null ? null : lastCommon.node;
            }
        }
        return null;
    }

    private NodeAndPosition getElement(ListNode head, int pos) {
        int i = 0;
        ListNode last = null;
        ListNode tmp = head;
        while (tmp != null && (pos < 0 || (i <= pos))) {
            i++;
            last = tmp;
            tmp = tmp.next;
        }
        return last == null ? null : new NodeAndPosition(last, i - 1);
    }

    public ListNode rotateRight(ListNode head, int k) {
        if (head == null) {
            return null;
        }
        NodeAndPosition last = getElement(head, -1);
        int size = last.pos + 1;
        int actual = k % size;
        if (actual == 0) {
            return head;
        }

        NodeAndPosition pre = getElement(head, (size - actual - 1));
        ListNode newHead = pre.node.next;
        pre.node.next = null;

        last.node.next = head;

        return newHead;
    }
}
