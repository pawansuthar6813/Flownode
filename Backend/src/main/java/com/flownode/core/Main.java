package com.flownode.core;


import com.flownode.core.execution.engine.WorkflowExecutor;
import com.flownode.core.execution.node.Node;
import com.flownode.core.execution.node.impl.condition.AmountGreaterThanConditionNode;
import com.flownode.core.execution.node.impl.lifecycle.DelayNode;
import com.flownode.core.execution.node.impl.lifecycle.EndNode;
import com.flownode.core.execution.node.impl.lifecycle.StartNode;
import com.flownode.core.execution.node.impl.task.ManualApprovalTaskNode;
import com.flownode.core.execution.node.impl.task.ScriptTaskNode;
import com.flownode.core.workflow.Workflow;

public class Main {

    public static void main(String[] args) throws Exception {

        // =====================================================
        // 1️⃣ Create Workflow
        // =====================================================

        Workflow workflow =
                new Workflow("wf-002", "Structural-Join-Test");

        // =====================================================
        // 2️⃣ Create Nodes
        // =====================================================

        Node start =
                new StartNode("start", "Start");

        Node initOrder =
                new ScriptTaskNode(
                        "init-order",
                        "Initialize Order",
                        ctx -> {
                            System.out.println(
                                    "Order created in system"
                            );

                            ctx.put("orderId", "ORD-123");
                        }
                );

        // Parent 1 → Payment
        Node paymentProcessing =
                new ScriptTaskNode(
                        "payment",
                        "Payment Processing",
                        ctx -> {
                            System.out.println(
                                    "Payment processed"
                            );

                            ctx.put("paymentStatus", "SUCCESS");
                        }
                );

        // Parent 2 → Inventory
        Node inventoryReservation =
                new DelayNode(
                        "inventory",
                        "Inventory Reservation",
                        2000   // simulate delay
                );

        // Join node → Shipping
        Node shipping =
                new ScriptTaskNode(
                        "shipping",
                        "Shipping Order",
                        ctx -> System.out.println(
                                "Order shipped"
                        )
                );

        Node end =
                new EndNode(
                        "end",
                        "End"
                );

        // =====================================================
        // 3️⃣ Register Nodes
        // =====================================================

        workflow.addNode(start);
        workflow.addNode(initOrder);
        workflow.addNode(paymentProcessing);
        workflow.addNode(inventoryReservation);
        workflow.addNode(shipping);
        workflow.addNode(end);

        // =====================================================
        // 4️⃣ Define Transitions
        // =====================================================

        workflow.setStartNodeId("start");

        workflow.addTransition("start", "init-order");

        // Parallel parents
        workflow.addTransition("init-order", "payment");
        workflow.addTransition("init-order", "inventory");

        // Join
        workflow.addTransition("payment", "shipping");
        workflow.addTransition("inventory", "shipping");

        workflow.addTransition("shipping", "end");

        // =====================================================
        // 5️⃣ Execute Workflow
        // =====================================================

        WorkflowExecutor executor =
                new WorkflowExecutor();

        executor.execute(workflow);

        System.out.println(
                "\nStructural Join Test Completed."
        );
    }
}

