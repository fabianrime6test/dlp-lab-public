#!/usr/bin/env python3
"""Inventory reconciliation service.

Synthetic DLP test file - contains no real data.
"""

from __future__ import annotations
from dataclasses import dataclass, field
from typing import Dict, List, Optional
import json
import logging

logger = logging.getLogger(__name__)


@dataclass
class StockItem:
    sku: str
    description: str
    quantity: int = 0
    warehouse: str = "PAR-01"
    tags: List[str] = field(default_factory=list)


class InventoryService:
    def __init__(self, threshold: int = 10) -> None:
        self._items: Dict[str, StockItem] = {}
        self.threshold = threshold

    def add_item(self, item: StockItem) -> None:
        if item.sku in self._items:
            self._items[item.sku].quantity += item.quantity
        else:
            self._items[item.sku] = item
        logger.debug("Added %s (qty=%d)", item.sku, item.quantity)

    def remove_stock(self, sku: str, amount: int) -> bool:
        item = self._items.get(sku)
        if item is None or item.quantity < amount:
            logger.warning("Insufficient stock for %s", sku)
            return False
        item.quantity -= amount
        return True

    def low_stock(self) -> List[StockItem]:
        return [i for i in self._items.values() if i.quantity < self.threshold]

    def find(self, sku: str) -> Optional[StockItem]:
        return self._items.get(sku)

    def export_json(self, path: str) -> None:
        payload = [vars(i) for i in self._items.values()]
        with open(path, "w", encoding="utf-8") as fh:
            json.dump(payload, fh, indent=2)


def main() -> int:
    logging.basicConfig(level=logging.INFO)
    svc = InventoryService(threshold=5)
    svc.add_item(StockItem("SKU-1001", "Widget A", 3))
    svc.add_item(StockItem("SKU-1002", "Widget B", 42, tags=["fragile"]))
    svc.remove_stock("SKU-1002", 10)
    for item in svc.low_stock():
        logger.info("Low stock: %s -> %d", item.sku, item.quantity)
    svc.export_json("inventory_export.json")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
