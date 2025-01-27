import asyncio
import os
import sys
from typing import List

import httpx

from textual.app import App, ComposeResult
from textual.binding import Binding
from textual.containers import VerticalScroll, Container, HorizontalGroup
from textual.widget import Widget
from textual.widgets import Header, ProgressBar, Static, Footer

REPO_URL = "https://repo.maven.apache.org/maven2"


def read_publications(version, path):
    files = [[x[0], list(filter(lambda file: file.endswith(".pom"), x[2]))[0]] for x in os.walk(path) if
             version in x[0]]
    paths = list(map(lambda tpl: Path(f"{tpl[0].removeprefix(path)}/{tpl[1]}"), files))
    return paths


class Path:
    def __init__(self, path) -> None:
        self.full = path
        self.base = os.path.basename(path)
        self.calculated_id = str(self.base.replace(".", "_"))


class Dependency(HorizontalGroup):
    def __init__(self, path: Path, *children: Widget) -> None:
        self.path = path
        super().__init__(*children, name=self.path.base)

    def compose(self) -> ComposeResult:
        yield Static(self.path.base, id=self.path.calculated_id)


class MonitoringApp(App):
    CSS = """
        Screen {
            align: center middle;
        }
    
        Header {
            text-align: center;
        }
    
        .progress-container {
            width: 80%;
            height: 3;
            margin: 1 0;
        }
    
        VerticalScroll {
            width: 95%;
            height: 80%;
            border: solid white;
        }
        
        .found {
            color: green;
        }
        
        .hidden {
            display: none;
        }
    """

    BINDINGS = [
        Binding(key="q", action="quit", description="Quit the app"),
        Binding(key="h", action="hide", description="Hide/Show Uploaded"),
    ]

    def __init__(self, paths: List[Path], version, *children, **kwargs):
        super().__init__(*children, **kwargs)
        self.paths = paths
        self.version = version

    def compose(self) -> ComposeResult:
        yield Header(show_clock=True, name="Monitoring")
        yield Container(
            ProgressBar(
                total=len(self.paths),
                show_percentage=True,
                id="progress-bar",
                show_eta=False,
            ),
            classes="progress-container",
        )

        deps = [Dependency(i) for i in self.paths]
        yield VerticalScroll(*deps)
        yield Footer()

    hidden = True

    def action_hide(self):
        if self.hidden:
            self.query(".found").remove_class("hidden")
        else:
            self.query(".found").add_class("hidden")

        self.hidden = not self.hidden

    def on_mount(self):
        self.title = "Monitoring Dependencies"
        self.sub_title = f"Version {self.version}"

    def on_load(self) -> None:
        self.run_worker(self.gather(), exclusive=True)

    async def gather(self):
        await asyncio.gather(*[self.update_weather(path) for path in _paths])

    lock = asyncio.Lock()
    progress = 0

    async def update_weather(self, path: Path) -> None:
        url = f"{REPO_URL}/{path.full}"
        async with httpx.AsyncClient() as client:
            response = await client.get(url)
            if response.status_code == 200:
                weather_widget = self.query_one(f"#{path.calculated_id}", Static)
                weather_widget.add_class("found")
                if self.hidden:
                    weather_widget.add_class("hidden")
                weather_widget.update(f"{path.base} - UPLOADED")
                await self.lock.acquire()
                self.progress += 1
                self.query_one("#progress-bar", ProgressBar).update(progress=self.progress)
                self.lock.release()
            else:
                await asyncio.sleep(1)
                await self.update_weather(path)


if __name__ == "__main__":
    if not os.path.exists("../build/repo/"):
        print("use ./publishLocal.sh from the repository root")
    elif len(sys.argv) != 2:
        print("Usage: python main.py <version>")
    else:
        _version = sys.argv[1]
        _paths = read_publications(_version, "../build/repo/")
        app = MonitoringApp(_paths, _version)
        app.run()
