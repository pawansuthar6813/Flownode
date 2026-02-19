package com.flownode.core;

import com.flownode.core.execution.engine.executor.ConcurrentWorkflowExecutor;
import com.flownode.core.execution.node.impl.condition.AmountGreaterThanConditionNode;
import com.flownode.core.execution.node.impl.condition.BooleanConditionNode;
import com.flownode.core.execution.node.impl.lifecycle.EndNode;
import com.flownode.core.execution.node.impl.lifecycle.StartNode;
import com.flownode.core.execution.node.impl.task.ScriptTaskNode;
import com.flownode.core.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // ─────────────────────────────────────────────────────────────
        //
        //                        start
        //                          │
        //                          A (amount > 5000?)
        //                      ┌───┴───┐
        //                   TRUE      FALSE
        //                     │          │
        //                  forkBC        D
        //                 ┌───┴───┐      │
        //                 B       C      │
        //                 └───┬───┘      │
        //                     E          │
        //               (region=US?)     │
        //               ┌────┴────┐      │
        //            TRUE        FALSE   │
        //              │            │    │
        //           forkFGH         I    │
        //          ┌──┬┴─┐          │    │
        //          F  G  H          │    │
        //          └──┬──┘          │    │
        //             J             │    │
        //         (risk>0.7?)       │    │
        //          ┌──┴──┐          │    │
        //       TRUE    FALSE       │    │
        //         │       │         │    │
        //         K     forkLM      │    │
        //         │    ┌──┴──┐      │    │
        //         │    L     M      │    │
        //         │    └──┬──┘      │    │
        //         │       N         │    │
        //         └───┬───┘         │    │
        //             O             │    │
        //             └──────┬──────┘    │
        //                    P           │
        //              (approved?)       │
        //               ┌────┴────┐      │
        //            TRUE        FALSE   │
        //              │            │    │
        //              Q            R    │
        //              └─────┬──────┘    │
        //                    └─────┬─────┘
        //                         end
        //
        // ─────────────────────────────────────────────────────────────

        // ── Lifecycle ──
        StartNode start = new StartNode("start", "Start");
        EndNode end     = new EndNode("end", "End");

        // ── Condition Nodes ──
        AmountGreaterThanConditionNode A = new AmountGreaterThanConditionNode(
                "A", "Check Amount > 5000", "amount", 5000
        );

        BooleanConditionNode E = new BooleanConditionNode(
                "E", "Check Region US", "isUS"
        );

        AmountGreaterThanConditionNode J = new AmountGreaterThanConditionNode(
                "J", "Check Risk > 0.7", "risk", 0.7
        );

        BooleanConditionNode P = new BooleanConditionNode(
                "P", "Check Final Approval", "finalApproved"
        );

        // ── Fork Nodes ──
        ScriptTaskNode forkBC = new ScriptTaskNode(
                "forkBC", "Fork → B and C",
                ctx -> System.out.println("[forkBC] Forking to B and C")
        );

        ScriptTaskNode forkFGH = new ScriptTaskNode(
                "forkFGH", "Fork → F, G and H",
                ctx -> System.out.println("[forkFGH] Forking to F, G and H")
        );

        ScriptTaskNode forkLM = new ScriptTaskNode(
                "forkLM", "Fork → L and M",
                ctx -> System.out.println("[forkLM] Forking to L and M")
        );

        // ── Task Nodes ──
        ScriptTaskNode B = new ScriptTaskNode("B", "Task B", ctx -> {
            System.out.println("[B] Executing — credit check");
            sleep(300);
            ctx.put("creditChecked", true);
        });

        ScriptTaskNode C = new ScriptTaskNode("C", "Task C", ctx -> {
            System.out.println("[C] Executing — identity verification");
            sleep(500);
            ctx.put("identityVerified", true);
        });

        ScriptTaskNode D = new ScriptTaskNode("D", "Task D", ctx -> {
            System.out.println("[D] Executing — low amount fast track");
            sleep(200);
        });

        ScriptTaskNode F = new ScriptTaskNode("F", "Task F", ctx -> {
            System.out.println("[F] Executing — US compliance check");
            sleep(400);
        });

        ScriptTaskNode G = new ScriptTaskNode("G", "Task G", ctx -> {
            System.out.println("[G] Executing — US tax validation");
            sleep(600);
        });

        ScriptTaskNode H = new ScriptTaskNode("H", "Task H", ctx -> {
            System.out.println("[H] Executing — US fraud detection");
            sleep(350);
        });

        ScriptTaskNode I = new ScriptTaskNode("I", "Task I", ctx -> {
            System.out.println("[I] Executing — international processing");
            sleep(400);
        });

        ScriptTaskNode K = new ScriptTaskNode("K", "Task K", ctx -> {
            System.out.println("[K] Executing — manual risk review");
            sleep(800);
        });

        ScriptTaskNode L = new ScriptTaskNode("L", "Task L", ctx -> {
            System.out.println("[L] Executing — automated risk mitigation 1");
            sleep(300);
        });

        ScriptTaskNode M = new ScriptTaskNode("M", "Task M", ctx -> {
            System.out.println("[M] Executing — automated risk mitigation 2");
            sleep(400);
        });

        ScriptTaskNode N = new ScriptTaskNode("N", "Task N", ctx ->
                System.out.println("[N] Executing — risk mitigation complete")
        );

        ScriptTaskNode O = new ScriptTaskNode("O", "Task O", ctx ->
                System.out.println("[O] Executing — pre-approval aggregation")
        );

        ScriptTaskNode Q = new ScriptTaskNode("Q", "Task Q", ctx ->
                System.out.println("[Q] Executing — approved: generate contract")
        );

        ScriptTaskNode R = new ScriptTaskNode("R", "Task R", ctx ->
                System.out.println("[R] Executing — rejected: notify customer")
        );

        // ── Build Workflow ──
        Workflow workflow = new Workflow("wf-ultra", "Ultra Complex Workflow Test");

        // ── Register Nodes ──
        workflow.addNode(start);
        workflow.addNode(A);
        workflow.addNode(forkBC);
        workflow.addNode(B);
        workflow.addNode(C);
        workflow.addNode(D);
        workflow.addNode(E);
        workflow.addNode(forkFGH);
        workflow.addNode(F);
        workflow.addNode(G);
        workflow.addNode(H);
        workflow.addNode(I);
        workflow.addNode(J);
        workflow.addNode(K);
        workflow.addNode(forkLM);
        workflow.addNode(L);
        workflow.addNode(M);
        workflow.addNode(N);
        workflow.addNode(O);
        workflow.addNode(P);
        workflow.addNode(Q);
        workflow.addNode(R);
        workflow.addNode(end);

        // ── Start Node ──
        workflow.setStartNodeId("start");

        // ── Transitions ──
        workflow.addTransition("start", "A");

        // A → TRUE:forkBC, FALSE:D
        workflow.addConditionTransition("A", "forkBC", "D");

        // forkBC → B, C (parallel)
        workflow.addTransition("forkBC", "B");
        workflow.addTransition("forkBC", "C");

        // B, C join → E
        workflow.addTransition("B", "E");
        workflow.addTransition("C", "E");

        // E → TRUE:forkFGH, FALSE:I
        workflow.addConditionTransition("E", "forkFGH", "I");

        // forkFGH → F, G, H (parallel)
        workflow.addTransition("forkFGH", "F");
        workflow.addTransition("forkFGH", "G");
        workflow.addTransition("forkFGH", "H");

        // F, G, H join → J
        workflow.addTransition("F", "J");
        workflow.addTransition("G", "J");
        workflow.addTransition("H", "J");

        // J → TRUE:K, FALSE:forkLM
        workflow.addConditionTransition("J", "K", "forkLM");

        // forkLM → L, M (parallel)
        workflow.addTransition("forkLM", "L");
        workflow.addTransition("forkLM", "M");

        // L, M join → N
        workflow.addTransition("L", "N");
        workflow.addTransition("M", "N");

        // K, N join → O
        workflow.addTransition("K", "O");
        workflow.addTransition("N", "O");

        // I, O join → P
        workflow.addTransition("I", "P");
        workflow.addTransition("O", "P");

        // P → TRUE:Q, FALSE:R
        workflow.addConditionTransition("P", "Q", "R");

        // D, Q, R join → end
        workflow.addTransition("D", "end");
        workflow.addTransition("Q", "end");
        workflow.addTransition("R", "end");

        // ── DEBUG: verify parent map ──
        System.out.println("Parents of end : " + workflow.getParents("end"));
        System.out.println("Parents of E   : " + workflow.getParents("E"));
        System.out.println("Parents of J   : " + workflow.getParents("J"));
        System.out.println("Parents of O   : " + workflow.getParents("O"));
        System.out.println("Parents of P   : " + workflow.getParents("P"));
        System.out.println();

        // ── Test Case 1: amount=6000, isUS=true, risk=0.9, finalApproved=true ──
        // Path: forkBC→B,C→E→forkFGH→F,G,H→J(TRUE)→K→O→P(TRUE)→Q→end
        // Expected executed  → [start,A,forkBC,B,C,E,forkFGH,F,G,H,J,K,O,P,Q,end]
        // Expected skipped   → [D,I,forkLM,L,M,N,R]
        System.out.println("========== TEST CASE 1: amount=6000, isUS=true, risk=0.9, finalApproved=true ==========");
        runWorkflow(workflow, 6000, true, 0.9, true);

        // ── Test Case 2: amount=6000, isUS=true, risk=0.5, finalApproved=false ──
        // Path: forkBC→B,C→E→forkFGH→F,G,H→J(FALSE)→forkLM→L,M→N→O→P(FALSE)→R→end
        // Expected executed  → [start,A,forkBC,B,C,E,forkFGH,F,G,H,J,forkLM,L,M,N,O,P,R,end]
        // Expected skipped   → [D,I,K,Q]
        System.out.println("\n========== TEST CASE 2: amount=6000, isUS=true, risk=0.5, finalApproved=false ==========");
        runWorkflow(workflow, 6000, true, 0.5, false);

        // ── Test Case 3: amount=6000, isUS=false, risk=0.5, finalApproved=true ──
        // Path: forkBC→B,C→E(FALSE)→I→P(TRUE)→Q→end
        // Expected executed  → [start,A,forkBC,B,C,E,I,P,Q,end]
        // Expected skipped   → [D,forkFGH,F,G,H,J,K,forkLM,L,M,N,R]
        System.out.println("\n========== TEST CASE 3: amount=6000, isUS=false, risk=0.5, finalApproved=true ==========");
        runWorkflow(workflow, 6000, false, 0.5, true);

        // ── Test Case 4: amount=3000, isUS=true, risk=0.9, finalApproved=true ──
        // Path: A(FALSE)→D→end
        // Expected executed  → [start,A,D,end]
        // Expected skipped   → [forkBC,B,C,E,forkFGH,F,G,H,I,J,K,forkLM,L,M,N,O,P,Q,R]
        System.out.println("\n========== TEST CASE 4: amount=3000 (FALSE branch only) ==========");
        runWorkflow(workflow, 3000, true, 0.9, true);
    }

    private static void runWorkflow(
            Workflow workflow,
            int amount,
            boolean isUS,
            double risk,
            boolean finalApproved) {

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("amount", amount);
        inputs.put("isUS", isUS);
        inputs.put("risk", risk);
        inputs.put("finalApproved", finalApproved);

        ConcurrentWorkflowExecutor executor = new ConcurrentWorkflowExecutor(8);
        executor.startWorkflow(workflow, inputs);

        try {
            executor.awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n===== EXECUTION SUMMARY =====");
        System.out.println("Executed Nodes → " + executor.getExecutedNodes());
        System.out.println("Skipped Nodes  → " + executor.getSkippedNodes());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}