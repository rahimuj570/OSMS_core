<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Routine Generation — Queue</title>

    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background-color: #5c0931;
            color: #fff;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }

        .card {
            width: min(520px, 95%);
            background: #fff;
            color: #1e1e1e;
            border-radius: 14px;
            padding: 2.5rem 2rem;
            text-align: center;
            box-shadow: 0 8px 30px rgba(0, 0, 0, .35);
        }

        /* ── Icon ── */
        .icon-wrap {
            margin: 0 auto 1.25rem;
            width: 56px;
            height: 56px;
            background: #5c0931;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .icon-wrap svg {
            width: 30px;
            height: 30px;
            fill: none;
            stroke: #fff;
            stroke-width: 2;
            stroke-linecap: round;
            stroke-linejoin: round;
        }

        /* ── Badge ── */
        .badge {
            display: inline-block;
            background: #fff3cd;
            color: #856404;
            font-size: .8rem;
            font-weight: 600;
            padding: .3rem .75rem;
            border-radius: 20px;
            margin-bottom: 1rem;
            letter-spacing: .02em;
        }

        .badge .dot {
            display: inline-block;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #856404;
            margin-right: .4rem;
            vertical-align: middle;
            animation: pulse 1.4s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50%      { opacity: .35; }
        }

        /* ── Typography ── */
        .card h1 {
            font-size: 1.35rem;
            font-weight: 700;
            color: #5c0931;
            margin-bottom: .6rem;
        }

        .card .desc {
            font-size: .92rem;
            line-height: 1.55;
            color: #444;
            max-width: 400px;
            margin: 0 auto 1.5rem;
        }

        /* ── Queue indicator ── */
        .queue-arrow {
            font-size: .85rem;
            color: #5c0931;
            margin-bottom: 1.25rem;
            letter-spacing: .08em;
        }

        .queue-arrow .arrow-down {
            display: block;
            font-size: 1.1rem;
            margin: .15rem 0;
            animation: bounce 1.2s ease-in-out infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50%      { transform: translateY(3px); }
        }

        .queue-item {
            font-weight: 600;
            font-size: .88rem;
        }

        .queue-item.active {
            color: #5c0931;
        }

        .queue-item.waiting {
            color: #6c757d;
        }

        /* ── Progress ── */
        .progress-outer {
            width: 100%;
            height: 26px;
            background: #e9ecef;
            border-radius: 13px;
            overflow: hidden;
            margin-bottom: .6rem;
            position: relative;
        }

        .progress-fill {
            height: 100%;
            width: 0%;
            border-radius: 13px;
            background: linear-gradient(90deg, #5c0931, #7a1a4a);
            transition: width 0.4s ease;
            position: relative;
        }

        /* Shimmer while running */
        .progress-fill.active::after {
            content: '';
            position: absolute;
            inset: 0;
            border-radius: 13px;
            background: linear-gradient(
                110deg,
                transparent 30%,
                rgba(255, 255, 255, .25) 50%,
                transparent 70%
            );
            background-size: 200% 100%;
            animation: shimmer 2s linear infinite;
        }

        @keyframes shimmer {
            0%   { background-position: 200% 0; }
            100% { background-position: -200% 0; }
        }

        .progress-label {
            display: flex;
            justify-content: space-between;
            align-items: baseline;
            margin-bottom: 1rem;
        }

        .progress-label .pct {
            font-size: 1.3rem;
            font-weight: 700;
            color: #5c0931;
        }

        .progress-label .sub {
            font-size: .78rem;
            color: #6c757d;
        }

        /* ── Status / footer ── */
        .status {
            font-size: .88rem;
            color: #555;
            min-height: 1.4em;
        }

        .status.done {
            color: #155724;
            font-weight: 600;
        }

        .status.err {
            color: #856404;
        }

        .footer {
            margin-top: 1.5rem;
            font-size: .78rem;
            color: #999;
        }
    </style>
</head>
<body>

<div class="card">

    <div class="icon-wrap">
        <!-- Calendar / schedule icon -->
        <svg viewBox="0 0 24 24">
            <rect x="3" y="4" width="18" height="17" rx="2"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <rect x="7" y="13" width="3" height="3" rx=".5"/>
            <rect x="13" y="13" width="3" height="3" rx=".5"/>
        </svg>
    </div>

    <div class="badge" id="badge">
        <span class="dot"></span> Waiting in generation queue
    </div>

    <h1>Routine Generation in Progress</h1>

    <p class="desc">
        Another routine is currently being generated by the scheduling system.<br>
        Your routine is next in line and will start automatically.
    </p>

    <div class="queue-arrow">
        <span class="queue-item active">Current Generation</span>
        <span class="arrow-down">▼</span>
        <span class="queue-item waiting">Your Routine</span>
    </div>

    <div class="progress-outer">
        <div id="progressBar" class="progress-fill active"></div>
    </div>

    <div class="progress-label">
        <span class="pct" id="percentage">0%</span>
        <span class="sub" id="subStatus">Generating routine…</span>
    </div>

    <p class="status" id="status">Checking generation status…</p>

    <p class="footer">
        Please keep this page open. No action required.
    </p>

</div>

<script>

const params = new URLSearchParams(window.location.search);
const efficiencyParam = encodeURIComponent(params.get('efficiency') || '');
const outsidePreferredParam = encodeURIComponent(params.get('outsidePreferred') || '');
const progressUrl = '<%= request.getContextPath() %>/GetRoutineProgressServlet';

function setUI(running, percentage) {
    var bar  = document.getElementById("progressBar");
    var pct  = document.getElementById("percentage");
    var sub  = document.getElementById("subStatus");
    var stat = document.getElementById("status");
    var badge = document.getElementById("badge");

    bar.style.width = percentage + "%";
    pct.textContent = percentage + "%";

    if (running) {
        bar.classList.add("active");
        sub.textContent = "Generating routine…";
        stat.textContent = "Please wait. Your routine will start automatically.";
        stat.className = "status";
        badge.innerHTML = '<span class="dot"></span> Waiting in generation queue';
    } else {
        bar.classList.remove("active");
        sub.textContent = "Complete";
        stat.textContent = "Current routine generation completed. Starting yours…";
        stat.className = "status done";
        badge.innerHTML = '✓ Generation complete';
    }
}

function checkGenerationStatus() {
    fetch(progressUrl)
        .then(function (r) { return r.json(); })
        .then(function (data) {
            var percentage = data.percentage || 0;

            if (data.running) {
                setUI(true, percentage);
                setTimeout(checkGenerationStatus, 1000);
            } else {
                setUI(false, 100);
                setTimeout(function () {
                    window.location.href =
                        '<%= request.getContextPath() %>/GenerateRoutineServlet'
                        + '?efficiency=' + efficiencyParam
                        + '&outsidePreferred=' + outsidePreferredParam;
                }, 1000);
            }
        })
        .catch(function (err) {
            console.error(err);
            document.getElementById("status").textContent =
                "Unable to check status. Retrying automatically…";
            document.getElementById("status").className = "status err";
            setTimeout(checkGenerationStatus, 2000);
        });
}

checkGenerationStatus();

</script>

</body>
</html>
